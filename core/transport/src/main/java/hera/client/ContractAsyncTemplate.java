/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.tupleorerror.FunctionChain.fail;
import static hera.util.IoUtils.from;
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
import hera.FutureChainer;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.AccountAsyncOperation;
import hera.api.ContractAsyncOperation;
import hera.api.SignAsyncOperation;
import hera.api.TransactionAsyncOperation;
import hera.api.encode.Decoder;
import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.api.model.ContractAddress;
import hera.api.model.ContractFunction;
import hera.api.model.ContractInferface;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
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
import hera.util.Base58Utils;
import hera.util.DangerousSupplier;
import hera.util.LittleEndianDataOutputStream;
import io.grpc.ManagedChannel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

  protected final SignAsyncOperation signAsyncOperation;

  protected final AccountAsyncOperation accountAsyncOperation;

  protected final TransactionAsyncOperation transactionAsyncOperation;

  protected final ModelConverter<AccountAddress, ByteString> accountAddressConverter;

  protected final ModelConverter<ContractTxReceipt, Blockchain.Receipt> receiptConverter;

  protected final ModelConverter<ContractInferface, Blockchain.ABI> contractInterfaceConverter;

  protected final ModelConverter<ContractResult, Rpc.SingleBytes> contractResultConverter;

  protected final Decoder base58Decoder =
      reader -> new ByteArrayInputStream(Base58Utils.decode(from(reader)));

  protected final ObjectMapper objectMapper = new ObjectMapper();

  public ContractAsyncTemplate(final ManagedChannel channel) {
    this(newFutureStub(channel));
  }

  /**
   * ContractAsyncTemplate constructor.
   *
   * @param aergoService aergo service
   */
  public ContractAsyncTemplate(final AergoRPCServiceFutureStub aergoService) {
    this(aergoService, new SignAsyncTemplate(aergoService), new AccountAsyncTemplate(aergoService),
        new TransactionAsyncTemplate(aergoService), new AccountAddressConverterFactory().create(),
        new ReceiptConverterFactory().create(), new ContractInterfaceConverterFactory().create(),
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
  public ResultOrErrorFuture<ContractTxHash> deploy(final AccountAddress creator,
      final DangerousSupplier<byte[]> rawContractCode) {
    return accountAsyncOperation.get(creator).map(a -> a.getNonce()).flatMap(nonce -> {
      try {
        final Transaction transaction = new Transaction();
        transaction.setNonce(1 + nonce);
        transaction.setSender(creator);

        final byte[] rawCode = rawContractCode.get();
        final ByteArrayOutputStream raw = new ByteArrayOutputStream();
        final LittleEndianDataOutputStream dataOut = new LittleEndianDataOutputStream(raw);
        dataOut.writeInt(rawCode.length + 4);
        dataOut.write(rawCode);
        dataOut.close();
        transaction.setPayload(BytesValue.of(raw.toByteArray()));

        return signAsyncOperation.sign(transaction).flatMap(signature -> {
          transaction.setSignature(signature);
          return transactionAsyncOperation.commit(transaction)
              .map(txHash -> txHash.adapt(ContractTxHash.class)
                  .orElseThrow(() -> new AdaptException(TxHash.class, ContractTxHash.class)));
        });

      } catch (Throwable e) {
        return ResultOrErrorFutureFactory.supply(() -> fail(e));
      }
    });
  }

  @Override
  public ResultOrErrorFuture<ContractInferface> getContractInterface(
      final ContractAddress contractAddress) {
    ResultOrErrorFuture<ContractInferface> nextFuture =
        ResultOrErrorFutureFactory.supplyEmptyFuture();

    final ByteString byteString = accountAddressConverter.convertToRpcModel(contractAddress);
    final Rpc.SingleBytes hashBytes = Rpc.SingleBytes.newBuilder().setValue(byteString).build();
    final ListenableFuture<Blockchain.ABI> listenableFuture = aergoService.getABI(hashBytes);
    FutureChainer<Blockchain.ABI, ContractInferface> callback =
        new FutureChainer<>(nextFuture, a -> contractInterfaceConverter.convertToDomainModel(a));
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;
  }

  @Override
  public ResultOrErrorFuture<ContractTxHash> execute(final AccountAddress executor,
      final ContractAddress contractAddress, final ContractFunction contractFunction,
      final Object... args) {
    try {
      final Transaction transaction = new Transaction();
      transaction.setSender(executor);
      transaction.setRecipient(contractAddress);
      transaction
          .setPayload(BytesValue.of(toFunctionCallJsonString(contractFunction, args).getBytes()));
      return transactionAsyncOperation.send(transaction)
          .map(txHash -> txHash.adapt(ContractTxHash.class)
              .orElseThrow(() -> new AdaptException(TxHash.class, ContractTxHash.class)));
    } catch (Throwable e) {
      return ResultOrErrorFutureFactory.supply(() -> fail(e));
    }
  }

  @Override
  public ResultOrErrorFuture<ContractResult> query(final ContractAddress contractAddress,
      final ContractFunction contractFunction, final Object... args) {
    ResultOrErrorFuture<ContractResult> nextFuture = ResultOrErrorFutureFactory.supplyEmptyFuture();

    ByteString queryInfo = null;
    try {
      queryInfo = ByteString.copyFrom(toFunctionCallJsonString(contractFunction, args).getBytes());
    } catch (JsonProcessingException e) {
      return ResultOrErrorFutureFactory.supply(() -> fail(e));
    }

    final Blockchain.Query query = Blockchain.Query.newBuilder()
        .setContractAddress(accountAddressConverter.convertToRpcModel(contractAddress))
        .setQueryinfo(queryInfo).build();
    final ListenableFuture<SingleBytes> listenableFuture = aergoService.queryContract(query);
    FutureChainer<SingleBytes, ContractResult> callback =
        new FutureChainer<>(nextFuture, s -> contractResultConverter.convertToDomainModel(s));
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;
  }

  protected String toFunctionCallJsonString(final ContractFunction contractFunction,
      final Object... args) throws JsonProcessingException {
    ObjectNode node = objectMapper.createObjectNode();
    node.put("Name", contractFunction.getName());
    ArrayNode argsNode = node.putArray("Args");
    for (Object arg : args) {
      argsNode.add(arg.toString());
    }
    return node.toString();
  }

}
