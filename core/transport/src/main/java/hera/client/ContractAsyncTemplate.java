/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.tupleorerror.FunctionChain.fail;
import static hera.util.TransportUtils.copyFrom;
import static org.slf4j.LoggerFactory.getLogger;
import static types.AergoRPCServiceGrpc.newFutureStub;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import hera.api.SignAsyncOperation;
import hera.api.TransactionAsyncOperation;
import hera.api.encode.Base58WithCheckSum;
import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.api.model.ContractAddress;
import hera.api.model.ContractCall;
import hera.api.model.ContractFunction;
import hera.api.model.ContractInterface;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.exception.AdaptException;
import hera.key.AergoKey;
import hera.strategy.SignStrategy;
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
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain;
import types.Rpc;
import types.Rpc.SingleBytes;

@ApiAudience.Private
@ApiStability.Unstable
@SuppressWarnings("unchecked")
@RequiredArgsConstructor
public class ContractAsyncTemplate implements ContractAsyncOperation {

  protected final Logger logger = getLogger(getClass());

  protected final AergoRPCServiceFutureStub aergoService;

  protected final Context context;

  protected final TransactionAsyncOperation transactionAsyncOperation;

  protected final ModelConverter<AccountAddress, ByteString> accountAddressConverter;

  protected final ModelConverter<ContractTxReceipt, Blockchain.Receipt> receiptConverter;

  protected final ModelConverter<ContractInterface, Blockchain.ABI> contractInterfaceConverter;

  protected final ModelConverter<ContractResult, Rpc.SingleBytes> contractResultConverter;

  protected final ObjectMapper objectMapper = new ObjectMapper();

  @Getter(lazy = true)
  private final SignAsyncOperation signAsyncOperation = context.getStrategy(SignStrategy.class)
      .map(s -> s.getSignOperation(aergoService.getChannel(), context))
      .flatMap(s -> s.adapt(SignAsyncOperation.class)).get();

  public ContractAsyncTemplate(final ManagedChannel channel, final Context context) {
    this(newFutureStub(channel), context);
  }

  /**
   * ContractAsyncTemplate constructor.
   *
   * @param aergoService aergo service
   */
  public ContractAsyncTemplate(final AergoRPCServiceFutureStub aergoService,
      final Context context) {
    this(aergoService, context, new TransactionAsyncTemplate(aergoService, context),
        new AccountAddressConverterFactory().create(), new ReceiptConverterFactory().create(),
        new ContractInterfaceConverterFactory().create(),
        new ContractResultConverterFactory().create());
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
  public ResultOrErrorFuture<ContractTxHash> deploy(final AergoKey key,
      final AccountAddress creator, final long nonce, final Base58WithCheckSum encodedPayload) {
    try {
      final byte[] rawPayloadWithVersion = encodedPayload.decode().getValue();
      if (logger.isTraceEnabled()) {
        logger.trace("Encoded contract deploy payload: {}", encodedPayload.getEncodedValue());
        logger.trace("Decoded contract deploy payload: {}", HexUtils.encode(rawPayloadWithVersion));
      }
      VersionUtils.validate(rawPayloadWithVersion, ContractInterface.PAYLOAD_VERSION);
      final byte[] rawPaylod = VersionUtils.trim(rawPayloadWithVersion);
      final ByteArrayOutputStream rawStream = new ByteArrayOutputStream();
      final LittleEndianDataOutputStream dataOut = new LittleEndianDataOutputStream(rawStream);
      dataOut.writeInt(rawPaylod.length + 4);
      dataOut.write(rawPaylod);
      dataOut.close();

      final Transaction transaction = new Transaction();
      transaction.setSender(creator);
      transaction.setNonce(nonce);
      transaction.setPayload(BytesValue.of(rawStream.toByteArray()));

      return getSignAsyncOperation().sign(key, transaction).flatMap(s -> {
        transaction.setSignature(s);
        return transactionAsyncOperation.commit(transaction)
            .map(txHash -> txHash.adapt(ContractTxHash.class)
                .orElseThrow(() -> new AdaptException(TxHash.class, ContractTxHash.class)));
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
        new FutureChainer<>(nextFuture, a -> contractInterfaceConverter.convertToDomainModel(a));
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;
  }

  @Override
  public ResultOrErrorFuture<ContractTxHash> execute(final AergoKey key,
      final AccountAddress executor, final long nonce, final ContractCall contractCall) {
    try {
      final Transaction transaction = new Transaction();
      transaction.setSender(executor);
      transaction.setRecipient(contractCall.getAddress());
      transaction.setNonce(nonce);
      final String functionCallString = toFunctionCallJsonString(contractCall);
      if (logger.isTraceEnabled()) {
        logger.trace("Contract execution address: {}, function: {}", contractCall.getAddress(),
            functionCallString);
      }
      transaction.setPayload(BytesValue.of(functionCallString.getBytes()));

      return getSignAsyncOperation().sign(key, transaction).flatMap(s -> {
        transaction.setSignature(s);
        return transactionAsyncOperation.commit(transaction)
            .map(txHash -> txHash.adapt(ContractTxHash.class)
                .orElseThrow(() -> new AdaptException(TxHash.class, ContractTxHash.class)));
      });
    } catch (Exception e) {
      return ResultOrErrorFutureFactory.supply(() -> fail(e));
    }
  }

  @Override
  public ResultOrErrorFuture<ContractResult> query(final ContractCall contractCall) {
    final ResultOrErrorFuture<ContractResult> nextFuture =
        ResultOrErrorFutureFactory.supplyEmptyFuture();

    ByteString queryInfo = null;
    try {
      final String functionCallString = toFunctionCallJsonString(contractCall);
      if (logger.isTraceEnabled()) {
        logger.trace("Contract query address: {}, function: {}", contractCall.getAddress(),
            functionCallString);
      }
      queryInfo = ByteString.copyFrom(functionCallString.getBytes());
    } catch (JsonProcessingException e) {
      return ResultOrErrorFutureFactory.supply(() -> fail(e));
    }

    final Blockchain.Query query = Blockchain.Query.newBuilder()
        .setContractAddress(accountAddressConverter.convertToRpcModel(contractCall.getAddress()))
        .setQueryinfo(queryInfo).build();
    final ListenableFuture<SingleBytes> listenableFuture = aergoService.queryContract(query);
    FutureChainer<SingleBytes, ContractResult> callback =
        new FutureChainer<>(nextFuture, s -> contractResultConverter.convertToDomainModel(s));
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;
  }

  protected String toFunctionCallJsonString(final ContractCall contractCall)
      throws JsonProcessingException {
    final ObjectNode node = objectMapper.createObjectNode();
    node.put("Name",
        Optional.ofNullable(contractCall.getFunction()).map(ContractFunction::getName).orElse(""));
    final ArrayNode argsNode = node.putArray("Args");
    contractCall.getArgs().stream().forEach(a -> argsNode.add(a.toString()));
    return node.toString();
  }

}
