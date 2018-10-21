/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.tupleorerror.FunctionChain.fail;
import static hera.util.TransportUtils.copyFrom;
import static org.slf4j.LoggerFactory.getLogger;
import static types.AergoRPCServiceGrpc.newFutureStub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.ByteString;
import hera.Context;
import hera.FutureChainer;
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
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.exception.AdaptException;
import hera.transport.AccountAddressConverterFactory;
import hera.transport.ContractInterfaceConverterFactory;
import hera.transport.ContractResultConverterFactory;
import hera.transport.ModelConverter;
import hera.transport.ReceiptConverterFactory;
import hera.util.HexUtils;
import hera.util.LittleEndianDataOutputStream;
import io.grpc.ManagedChannel;
import java.io.ByteArrayOutputStream;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.Getter;
import org.slf4j.Logger;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain;
import types.Rpc;
import types.Rpc.SingleBytes;

@ApiAudience.Private
@ApiStability.Unstable
@SuppressWarnings("unchecked")
public class ContractAsyncTemplate implements ContractAsyncOperation, ChannelInjectable {

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

  protected Context context;

  @Getter
  protected AergoRPCServiceFutureStub aergoService;

  protected AccountAsyncTemplate accountAsyncOperation = new AccountAsyncTemplate();

  protected TransactionAsyncTemplate transactionAsyncOperation = new TransactionAsyncTemplate();

  @Override
  public void setContext(final Context context) {
    this.context = context;
    accountAsyncOperation.setContext(context);
    transactionAsyncOperation.setContext(context);
  }

  @Override
  public void injectChannel(final ManagedChannel channel) {
    this.aergoService = newFutureStub(channel);
    accountAsyncOperation.injectChannel(channel);
    transactionAsyncOperation.injectChannel(channel);
  }

  @Override
  public ResultOrErrorFuture<ContractTxReceipt> getReceipt(final ContractTxHash deployTxHash) {
    ResultOrErrorFuture<ContractTxReceipt> nextFuture =
        ResultOrErrorFutureFactory.supplyEmptyFuture();

    final ByteString byteString = copyFrom(deployTxHash.getBytesValue());
    final Rpc.SingleBytes hashBytes = Rpc.SingleBytes.newBuilder().setValue(byteString).build();
    final ListenableFuture<Blockchain.Receipt> listenableFuture =
        aergoService.getReceipt(hashBytes);
    FutureChainer<Blockchain.Receipt, ContractTxReceipt> callback =
        new FutureChainer<>(nextFuture, r -> receiptConverter.convertToDomainModel(r));
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;
  }

  @Override
  public ResultOrErrorFuture<ContractTxHash> deploy(final Account creator,
      final ContractDefinition contractDefinition, final Fee fee) {
    try {
      final byte[] rawPayloadWithVersion =
          contractDefinition.getEncodedContract().decode().getValue();
      if (logger.isTraceEnabled()) {
        logger.trace("Encoded contract deploy payload: {}",
            contractDefinition.getEncodedContract().getEncodedValue());
        logger.trace("Decoded contract deploy payload: {}", HexUtils.encode(rawPayloadWithVersion));
      }
      VersionUtils.validate(rawPayloadWithVersion, ContractInterface.PAYLOAD_VERSION);

      final byte[] rawPaylod = VersionUtils.trim(rawPayloadWithVersion);
      final ByteArrayOutputStream rawStream = new ByteArrayOutputStream();
      final LittleEndianDataOutputStream dataOut = new LittleEndianDataOutputStream(rawStream);
      dataOut.writeInt(rawPaylod.length + 4);
      dataOut.write(rawPaylod);
      if (contractDefinition.getConstructorArgs().length > 0) {
        final ArrayNode constructorArgs =
            getArgsByJsonArray(contractDefinition.getConstructorArgs());
        logger.trace("Contract constructor args: {}", constructorArgs.toString());
        dataOut.write(constructorArgs.toString().getBytes());
      }
      dataOut.close();

      final Transaction transaction = new Transaction();
      transaction.setSender(creator);
      transaction.setNonce(creator.nextNonce());
      transaction.setPayload(BytesValue.of(rawStream.toByteArray()));
      transaction.setFee(fee);

      return accountAsyncOperation.sign(creator, transaction).flatMap(s -> {
        transaction.setSignature(s);
        return transactionAsyncOperation.commit(transaction)
            .map(txHash -> txHash.adapt(ContractTxHash.class).<AdaptException>orElseThrow(
                () -> new AdaptException(TxHash.class, ContractTxHash.class)));
      });
    } catch (Exception e) {
      return ResultOrErrorFutureFactory.supply(() -> fail(e));
    }
  }

  @Override
  public ResultOrErrorFuture<ContractInterface> getContractInterface(
      final ContractAddress contractAddress) {
    ResultOrErrorFuture<ContractInterface> nextFuture =
        ResultOrErrorFutureFactory.supplyEmptyFuture();

    final ByteString byteString = accountAddressConverter.convertToRpcModel(contractAddress);
    final Rpc.SingleBytes hashBytes = Rpc.SingleBytes.newBuilder().setValue(byteString).build();
    final ListenableFuture<Blockchain.ABI> listenableFuture = aergoService.getABI(hashBytes);
    FutureChainer<Blockchain.ABI, ContractInterface> callback =
        new FutureChainer<>(nextFuture, a -> {
          final ContractInterface contractInterface =
              contractInterfaceConverter.convertToDomainModel(a);
          contractInterface.setContractAddress(contractAddress);
          return contractInterface;
        });
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;
  }

  @Override
  public ResultOrErrorFuture<ContractTxHash> execute(final Account executor,
      final ContractInvocation contractInvocation, final Fee fee) {
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

      return accountAsyncOperation.sign(executor, transaction).flatMap(s -> {
        transaction.setSignature(s);
        return transactionAsyncOperation.commit(transaction)
            .map(txHash -> txHash.adapt(ContractTxHash.class).<AdaptException>orElseThrow(
                () -> new AdaptException(TxHash.class, ContractTxHash.class)));
      });
    } catch (Exception e) {
      return ResultOrErrorFutureFactory.supply(() -> fail(e));
    }
  }

  @Override
  public ResultOrErrorFuture<ContractResult> query(final ContractInvocation contractInvocation) {
    try {
      final ResultOrErrorFuture<ContractResult> nextFuture =
          ResultOrErrorFutureFactory.supplyEmptyFuture();

      final String functionCallString = toFunctionCallJsonString(contractInvocation);
      if (logger.isTraceEnabled()) {
        logger.trace("Contract query address: {}, function: {}", contractInvocation.getAddress(),
            functionCallString);
      }
      final Blockchain.Query query = Blockchain.Query.newBuilder()
          .setContractAddress(
              accountAddressConverter.convertToRpcModel(contractInvocation.getAddress()))
          .setQueryinfo(ByteString.copyFrom(functionCallString.getBytes())).build();
      final ListenableFuture<SingleBytes> listenableFuture = aergoService.queryContract(query);
      FutureChainer<SingleBytes, ContractResult> callback =
          new FutureChainer<>(nextFuture, s -> contractResultConverter.convertToDomainModel(s));

      Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

      return nextFuture;
    } catch (Exception e) {
      return ResultOrErrorFutureFactory.supply(() -> fail(e));
    }
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

}
