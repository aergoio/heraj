/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static com.google.common.base.Suppliers.memoize;
import static com.google.common.util.concurrent.Futures.addCallback;
import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static hera.api.tupleorerror.FunctionChain.fail;
import static hera.api.tupleorerror.FunctionChain.of;
import static hera.util.TransportUtils.copyFrom;
import static org.slf4j.LoggerFactory.getLogger;
import static types.AergoRPCServiceGrpc.newFutureStub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.ByteString;
import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.VersionUtils;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.ContractAsyncOperation;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractFunction;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.model.Fee;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.tupleorerror.Function1;
import hera.api.tupleorerror.Function3;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.exception.AdaptException;
import hera.strategy.StrategyChain;
import hera.transport.AccountAddressConverterFactory;
import hera.transport.ContractInterfaceConverterFactory;
import hera.transport.ContractResultConverterFactory;
import hera.transport.ModelConverter;
import hera.transport.ReceiptConverterFactory;
import hera.util.HexUtils;
import hera.util.LittleEndianDataOutputStream;
import io.grpc.ManagedChannel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Getter;
import org.slf4j.Logger;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain;
import types.Rpc;

@ApiAudience.Private
@ApiStability.Unstable
@SuppressWarnings("unchecked")
public class ContractAsyncTemplate
    implements ContractAsyncOperation, ChannelInjectable, ContextProviderInjectable {

  protected final Logger logger = getLogger(getClass());

  protected final ModelConverter<AccountAddress, ByteString> accountAddressConverter =
      new AccountAddressConverterFactory().create();

  protected final ModelConverter<ContractTxReceipt, Blockchain.Receipt> receiptConverter =
      new ReceiptConverterFactory().create();

  protected final ModelConverter<ContractInterface, Blockchain.ABI> contractInterfaceConverter =
      new ContractInterfaceConverterFactory().create();

  protected final ModelConverter<ContractResult, Rpc.SingleBytes> contractResultConverter =
      new ContractResultConverterFactory().create();

  protected final ObjectMapper objectMapper = new ObjectMapper();

  @Getter
  protected AergoRPCServiceFutureStub aergoService;

  protected AccountAsyncTemplate accountAsyncOperation = new AccountAsyncTemplate();

  protected TransactionAsyncTemplate transactionAsyncOperation = new TransactionAsyncTemplate();

  protected ContextProvider contextProvider;

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final StrategyChain strategyChain = StrategyChain.of(contextProvider.get());

  @Override
  public void setChannel(final ManagedChannel channel) {
    this.aergoService = newFutureStub(channel);
    accountAsyncOperation.setChannel(channel);
    transactionAsyncOperation.setChannel(channel);
  }

  @Override
  public void setContextProvider(final ContextProvider contextProvider) {
    this.contextProvider = contextProvider;
    accountAsyncOperation.setContextProvider(contextProvider);
    transactionAsyncOperation.setContextProvider(contextProvider);
  }

  private final Function1<ContractTxHash,
      ResultOrErrorFuture<ContractTxReceipt>> getReceiptFunction =
          (deployTxHash) -> {
            ResultOrErrorFuture<ContractTxReceipt> nextFuture =
                ResultOrErrorFutureFactory.supplyEmptyFuture();

            final ByteString byteString = copyFrom(deployTxHash.getBytesValue());
            final Rpc.SingleBytes hashBytes =
                Rpc.SingleBytes.newBuilder().setValue(byteString).build();
            final ListenableFuture<Blockchain.Receipt> listenableFuture =
                aergoService.getReceipt(hashBytes);
            FutureChain<Blockchain.Receipt, ContractTxReceipt> callback =
                new FutureChain<>(nextFuture);
            callback
                .setSuccessHandler(
                    receipt -> of(() -> receiptConverter.convertToDomainModel(receipt)));
            addCallback(listenableFuture, callback, directExecutor());

            return nextFuture;
          };

  private final Function3<Account, ContractDefinition, Fee,
      ResultOrErrorFuture<ContractTxHash>> deployFunction =
          (creator, contractDefinition, fee) -> {
            try {
              final Transaction transaction = new Transaction();
              transaction.setSender(creator);
              transaction.setNonce(creator.nextNonce());
              transaction.setPayload(definitionToPayloadForm(contractDefinition));
              transaction.setFee(fee);

              return signAndCommit(creator, transaction);
            } catch (Throwable e) {
              ResultOrErrorFuture<ContractTxHash> next =
                  ResultOrErrorFutureFactory.supplyEmptyFuture();
              next.complete(fail(e));
              return next;
            }
          };

  private final Function1<ContractAddress,
      ResultOrErrorFuture<ContractInterface>> getContractInterfaceFunction = (contractAddress) -> {
        ResultOrErrorFuture<ContractInterface> nextFuture =
            ResultOrErrorFutureFactory.supplyEmptyFuture();

        final ByteString byteString =
            accountAddressConverter.convertToRpcModel(contractAddress);
        final Rpc.SingleBytes hashBytes =
            Rpc.SingleBytes.newBuilder().setValue(byteString).build();
        final ListenableFuture<Blockchain.ABI> listenableFuture =
            aergoService.getABI(hashBytes);
        FutureChain<Blockchain.ABI, ContractInterface> callback = new FutureChain<>(nextFuture);
        callback.setSuccessHandler(abi -> of(() -> {
          final ContractInterface contractInterface =
              contractInterfaceConverter.convertToDomainModel(abi);
          contractInterface.setContractAddress(contractAddress);
          return contractInterface;
        }));
        addCallback(listenableFuture, callback, directExecutor());

        return nextFuture;
      };

  private final Function3<Account, ContractInvocation, Fee,
      ResultOrErrorFuture<ContractTxHash>> executeFunction =
          (executor, contractInvocation, fee) -> {
            try {
              final Transaction transaction = new Transaction();
              transaction.setSender(executor);
              transaction.setRecipient(contractInvocation.getAddress());
              transaction.setNonce(executor.nextNonce());
              final String functionCallString = toFunctionCallJsonString(contractInvocation);
              if (logger.isTraceEnabled()) {
                logger.trace("Contract execution address: {}, function: {}",
                    contractInvocation.getAddress(), functionCallString);
              }
              transaction.setPayload(BytesValue.of(functionCallString.getBytes()));
              transaction.setFee(fee);

              return signAndCommit(executor, transaction);
            } catch (Exception e) {
              ResultOrErrorFuture<ContractTxHash> next =
                  ResultOrErrorFutureFactory.supplyEmptyFuture();
              next.complete(fail(e));
              return next;
            }
          };

  private final Function1<ContractInvocation, ResultOrErrorFuture<ContractResult>> queryFunction =
      (contractInvocation) -> {
        try {
          final ResultOrErrorFuture<ContractResult> nextFuture =
              ResultOrErrorFutureFactory.supplyEmptyFuture();

          final String functionCallString = toFunctionCallJsonString(contractInvocation);
          if (logger.isTraceEnabled()) {
            logger.trace("Contract query address: {}, function: {}",
                contractInvocation.getAddress(),
                functionCallString);
          }
          final Blockchain.Query query = Blockchain.Query.newBuilder()
              .setContractAddress(
                  accountAddressConverter.convertToRpcModel(contractInvocation.getAddress()))
              .setQueryinfo(ByteString.copyFrom(functionCallString.getBytes())).build();
          final ListenableFuture<Rpc.SingleBytes> listenableFuture =
              aergoService.queryContract(query);
          FutureChain<Rpc.SingleBytes, ContractResult> callback = new FutureChain<>(nextFuture);
          callback.setSuccessHandler(
              result -> of(() -> contractResultConverter.convertToDomainModel(result)));
          addCallback(listenableFuture, callback, directExecutor());

          return nextFuture;
        } catch (Exception e) {
          ResultOrErrorFuture<ContractResult> next =
              ResultOrErrorFutureFactory.supplyEmptyFuture();
          next.complete(fail(e));
          return next;
        }
      };

  protected BytesValue definitionToPayloadForm(final ContractDefinition contractDefinition)
      throws IOException {
    final byte[] rawPayloadWithVersion =
        contractDefinition.getEncodedContract().decode().getValue();
    if (logger.isTraceEnabled()) {
      logger.trace("Encoded contract deploy payload: {}",
          contractDefinition.getEncodedContract().getEncodedValue());
      logger.trace("Decoded contract deploy payload: {}", HexUtils.encode(rawPayloadWithVersion));
    }
    VersionUtils.validate(rawPayloadWithVersion, ContractDefinition.PAYLOAD_VERSION);

    final byte[] rawPaylod = VersionUtils.trim(rawPayloadWithVersion);
    final ByteArrayOutputStream rawStream = new ByteArrayOutputStream();
    final LittleEndianDataOutputStream dataOut = new LittleEndianDataOutputStream(rawStream);
    dataOut.writeInt(rawPaylod.length + 4);
    dataOut.write(rawPaylod);
    if (contractDefinition.getConstructorArgs().length > 0) {
      final ArrayNode constructorArgs = getArgsByJsonArray(contractDefinition.getConstructorArgs());
      logger.trace("Contract constructor args: {}", constructorArgs.toString());
      dataOut.write(constructorArgs.toString().getBytes());
    }
    dataOut.close();
    final BytesValue definitionPayload = BytesValue.of(rawStream.toByteArray());
    return definitionPayload;
  }


  protected ResultOrErrorFuture<ContractTxHash> signAndCommit(final Account account,
      final Transaction transaction) {
    return accountAsyncOperation.sign(account, transaction).flatMap(signature -> {
      transaction.setSignature(signature);
      return transactionAsyncOperation.commit(transaction)
          .map(txHash -> txHash.adapt(ContractTxHash.class)
              .orElseThrow(() -> new AdaptException(TxHash.class, ContractTxHash.class)));
    });
  }

  protected String toFunctionCallJsonString(final ContractInvocation contractInvocation) {
    final ObjectNode node = objectMapper.createObjectNode();
    node.put("Name", Optional.ofNullable(contractInvocation.getFunction())
        .map(ContractFunction::getName).orElse(""));
    node.set("Args", getArgsByJsonArray(contractInvocation.getArgs()));
    return node.toString();
  }

  protected ArrayNode getArgsByJsonArray(final Object[] args) {
    final ArrayNode argsNode = objectMapper.createArrayNode();
    Stream.of(args).forEach(a -> {
      if (a instanceof Integer) {
        argsNode.add((Integer) a);
      } else if (a instanceof Long) {
        argsNode.add((Long) a);
      } else if (a instanceof Float) {
        argsNode.add((Float) a);
      } else if (a instanceof Double) {
        argsNode.add((Double) a);
      } else if (a instanceof Boolean) {
        argsNode.add((Boolean) a);
      } else if (a instanceof String) {
        argsNode.add(a.toString());
      } else {
        throw new IllegalArgumentException("Args type must be number or string");
      }
    });
    return argsNode;
  }

  protected Supplier<Function1<ContractTxHash,
      ResultOrErrorFuture<ContractTxReceipt>>> getReceiptFunctionSupplier =
          memoize(() -> getStrategyChain().apply(getReceiptFunction));

  protected Supplier<Function3<Account, ContractDefinition, Fee,
      ResultOrErrorFuture<ContractTxHash>>> deployFunctionSupplier =
          memoize(() -> getStrategyChain().apply(deployFunction));

  protected Supplier<Function1<ContractAddress,
      ResultOrErrorFuture<ContractInterface>>> getContractInterfaceFunctionSupplier =
          memoize(() -> getStrategyChain().apply(getContractInterfaceFunction));

  protected Supplier<Function3<Account, ContractInvocation, Fee,
      ResultOrErrorFuture<ContractTxHash>>> executeFunctionSupplier =
          memoize(() -> getStrategyChain().apply(executeFunction));

  protected Supplier<
      Function1<ContractInvocation, ResultOrErrorFuture<ContractResult>>> queryFunctionSupplier =
          memoize(() -> getStrategyChain().apply(queryFunction));

  @Override
  public ResultOrErrorFuture<ContractTxReceipt> getReceipt(final ContractTxHash contractTxHash) {
    return getReceiptFunctionSupplier.get().apply(contractTxHash);
  }

  @Override
  public ResultOrErrorFuture<ContractTxHash> deploy(final Account creator,
      final ContractDefinition contractDefinition, final Fee fee) {
    return deployFunctionSupplier.get().apply(creator, contractDefinition, fee);
  }

  @Override
  public ResultOrErrorFuture<ContractInterface> getContractInterface(
      final ContractAddress contractAddress) {
    return getContractInterfaceFunctionSupplier.get().apply(contractAddress);
  }

  @Override
  public ResultOrErrorFuture<ContractTxHash> execute(final Account executor,
      final ContractInvocation contractInvocation, final Fee fee) {
    return executeFunctionSupplier.get().apply(executor, contractInvocation, fee);
  }

  @Override
  public ResultOrErrorFuture<ContractResult> query(final ContractInvocation contractInvocation) {
    return queryFunctionSupplier.get().apply(contractInvocation);
  }



}
