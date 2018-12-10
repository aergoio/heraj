/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

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
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
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
import hera.api.model.RawTransaction;
import hera.api.model.TxHash;
import hera.api.tupleorerror.Function1;
import hera.api.tupleorerror.Function4;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.exception.AdaptException;
import hera.transport.AccountAddressConverterFactory;
import hera.transport.ContractInterfaceConverterFactory;
import hera.transport.ContractResultConverterFactory;
import hera.transport.ModelConverter;
import hera.transport.ReceiptConverterFactory;
import hera.util.Base58Utils;
import hera.util.HexUtils;
import hera.util.LittleEndianDataOutputStream;
import hera.util.VersionUtils;
import io.grpc.ManagedChannel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import org.slf4j.Logger;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain;
import types.Rpc;

@ApiAudience.Private
@ApiStability.Unstable
@SuppressWarnings("unchecked")
public class ContractBaseTemplate implements ChannelInjectable {

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

  protected AccountBaseTemplate accountBaseTemplate = new AccountBaseTemplate();

  protected TransactionBaseTemplate transactionBaseTemplate = new TransactionBaseTemplate();

  @Override
  public void setChannel(final ManagedChannel channel) {
    this.aergoService = newFutureStub(channel);
    accountBaseTemplate.setChannel(channel);
    transactionBaseTemplate.setChannel(channel);
  }

  @Getter
  private final Function1<ContractTxHash,
      ResultOrErrorFuture<ContractTxReceipt>> receiptFunction =
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

  @Getter
  private final Function4<Account, ContractDefinition, Long, Fee,
      ResultOrErrorFuture<ContractTxHash>> deployFunction =
          (creator, contractDefinition, nonce, fee) -> {
            try {
              final RawTransaction rawTransaction = RawTransaction.newBuilder()
                  .sender(creator)
                  .recipient(AccountAddress.of(BytesValue.EMPTY))
                  .amount(BigInteger.ZERO)
                  .nonce(nonce)
                  .fee(fee)
                  .payload(definitionToPayloadForm(contractDefinition))
                  .build();
              return signAndCommit(creator, rawTransaction);
            } catch (Throwable e) {
              ResultOrErrorFuture<ContractTxHash> next =
                  ResultOrErrorFutureFactory.supplyEmptyFuture();
              next.complete(fail(e));
              return next;
            }
          };

  @Getter
  private final Function1<ContractAddress,
      ResultOrErrorFuture<ContractInterface>> contractInterfaceFunction = (contractAddress) -> {
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
          final ContractInterface withoutAddress =
              contractInterfaceConverter.convertToDomainModel(abi);
          return new ContractInterface(contractAddress, withoutAddress.getVersion(),
              withoutAddress.getLanguage(), withoutAddress.getFunctions());
        }));
        addCallback(listenableFuture, callback, directExecutor());

        return nextFuture;
      };

  @Getter
  private final Function4<Account, ContractInvocation, Long, Fee,
      ResultOrErrorFuture<ContractTxHash>> executeFunction =
          (executor, contractInvocation, nonce, fee) -> {
            try {
              final String functionCallString = toFunctionCallJsonString(contractInvocation);
              if (logger.isTraceEnabled()) {
                logger.trace("Contract execution address: {}, function: {}",
                    contractInvocation.getAddress(), functionCallString);
              }
              final RawTransaction rawTransaction = RawTransaction.newBuilder()
                  .sender(executor)
                  .recipient(contractInvocation.getAddress())
                  .amount(BigInteger.ZERO)
                  .nonce(nonce)
                  .fee(fee)
                  .payload(BytesValue.of(functionCallString.getBytes()))
                  .build();
              return signAndCommit(executor, rawTransaction);
            } catch (Exception e) {
              ResultOrErrorFuture<ContractTxHash> next =
                  ResultOrErrorFutureFactory.supplyEmptyFuture();
              next.complete(fail(e));
              return next;
            }
          };

  @Getter
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
        Base58Utils.decodeWithCheck(contractDefinition.getEncodedContract());
    if (logger.isTraceEnabled()) {
      logger.trace("Encoded contract deploy payload: {}", contractDefinition.getEncodedContract());
      logger.trace("Decoded contract deploy payload: {}", HexUtils.encode(rawPayloadWithVersion));
    }
    VersionUtils.validate(rawPayloadWithVersion, ContractDefinition.PAYLOAD_VERSION);

    final byte[] rawPaylod = VersionUtils.trim(rawPayloadWithVersion);
    final ByteArrayOutputStream rawStream = new ByteArrayOutputStream();
    final LittleEndianDataOutputStream dataOut = new LittleEndianDataOutputStream(rawStream);
    dataOut.writeInt(rawPaylod.length + 4);
    dataOut.write(rawPaylod);
    if (!contractDefinition.getConstructorArgs().isEmpty()) {
      final ArrayNode constructorArgs = getArgsByJsonArray(contractDefinition.getConstructorArgs());
      logger.trace("Contract constructor args: {}", constructorArgs.toString());
      dataOut.write(constructorArgs.toString().getBytes());
    }
    dataOut.close();
    final BytesValue definitionPayload = BytesValue.of(rawStream.toByteArray());
    return definitionPayload;
  }


  protected ResultOrErrorFuture<ContractTxHash> signAndCommit(final Account account,
      final RawTransaction transaction) {
    return accountBaseTemplate.getSignFunction().apply(account, transaction)
        .flatMap(signedTransaction -> transactionBaseTemplate.getCommitFunction()
            .apply(signedTransaction)
            .map(txHash -> txHash.adapt(ContractTxHash.class)
                .orElseThrow(() -> new AdaptException(TxHash.class, ContractTxHash.class))));
  }

  protected String toFunctionCallJsonString(final ContractInvocation contractInvocation) {
    final ObjectNode node = objectMapper.createObjectNode();
    node.put("Name", Optional.ofNullable(contractInvocation.getFunction())
        .map(ContractFunction::getName).orElse(""));
    node.set("Args", getArgsByJsonArray(contractInvocation.getArgs()));
    return node.toString();
  }

  protected ArrayNode getArgsByJsonArray(final List<Object> args) {
    final ArrayNode argsNode = objectMapper.createArrayNode();
    for (Object arg : args) {
      if (arg instanceof Integer) {
        argsNode.add((Integer) arg);
      } else if (arg instanceof Long) {
        argsNode.add((Long) arg);
      } else if (arg instanceof Float) {
        argsNode.add((Float) arg);
      } else if (arg instanceof Double) {
        argsNode.add((Double) arg);
      } else if (arg instanceof Boolean) {
        argsNode.add((Boolean) arg);
      } else if (arg instanceof String) {
        argsNode.add(arg.toString());
      } else {
        throw new IllegalArgumentException("Args type must be number or string");
      }
    }
    return argsNode;
  }

}
