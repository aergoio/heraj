/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.tupleorerror.FunctionChain.success;
import static hera.util.TransportUtils.copyFrom;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.slf4j.LoggerFactory.getLogger;
import static types.AergoRPCServiceGrpc.newFutureStub;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.ByteString;
import hera.FutureChainer;
import hera.api.TransactionAsyncOperation;
import hera.api.model.BytesValue;
import hera.api.model.Hash;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.transport.ModelConverter;
import hera.transport.TransactionConverterFactory;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain.Tx;
import types.Blockchain.TxList;
import types.Rpc.CommitResultList;
import types.Rpc.CommitStatus;
import types.Rpc.SingleBytes;
import types.Rpc.VerifyResult;

@RequiredArgsConstructor
public class TransactionAsyncTemplate implements TransactionAsyncOperation {

  protected final Logger logger = getLogger(getClass());

  protected final AergoRPCServiceFutureStub aergoService;

  protected final ModelConverter<Transaction, Tx> transactionConverter;

  public TransactionAsyncTemplate(final ManagedChannel channel) {
    this(newFutureStub(channel));
  }

  public TransactionAsyncTemplate(final AergoRPCServiceFutureStub aergoService) {
    this(aergoService, new TransactionConverterFactory().create());
  }

  @Override
  public ResultOrErrorFuture<Optional<Transaction>> getTransaction(final Hash hash) {
    ResultOrErrorFuture<Optional<Transaction>> nextFuture = new ResultOrErrorFuture<>();

    final ByteString byteString = copyFrom(hash);
    final SingleBytes bytes = SingleBytes.newBuilder().setValue(byteString).build();
    ListenableFuture<Tx> listenableFuture = aergoService.getTX(bytes);
    FutureChainer<Tx, Optional<Transaction>> callback =
        new FutureChainer<Tx, Optional<Transaction>>(nextFuture,
            tx -> ofNullable(transactionConverter.convertToDomainModel(tx))) {
          // TODO : remove it
          @Override
          public void onFailure(Throwable throwable) {
            if (throwable instanceof StatusRuntimeException) {
              StatusRuntimeException e = (StatusRuntimeException) throwable;
              if (ofNullable(e.getStatus()).map(Status::getCode)
                  .filter(code -> Status.NOT_FOUND.getCode() == code).isPresent()) {
                getNextFuture().complete(success(empty()));
              }
            }
          }
        };
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;
  }

  @Override
  public ResultOrErrorFuture<Signature> sign(final Transaction transaction) {
    ResultOrErrorFuture<Signature> nextFuture = new ResultOrErrorFuture<>();

    final Tx rpcTransaction = transactionConverter.convertToRpcModel(transaction);
    final ListenableFuture<Tx> listenableFuture = aergoService.signTX(rpcTransaction);
    FutureChainer<Tx, Signature> callback = new FutureChainer<>(nextFuture, tx -> {
      final BytesValue sign = ofNullable(tx.getBody().getSign()).map(ByteString::toByteArray)
          .filter(bytes -> 0 != bytes.length).map(BytesValue::of)
          .orElseThrow(IllegalArgumentException::new);
      final Hash hash =
          ofNullable(tx.getHash()).map(ByteString::toByteArray).filter(bytes -> 0 != bytes.length)
              .map(Hash::new).orElseThrow(IllegalArgumentException::new);
      return Signature.of(sign, hash);
    });
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;
  }

  @Override
  public ResultOrErrorFuture<Boolean> verify(final Transaction transaction) {
    ResultOrErrorFuture<Boolean> nextFuture = new ResultOrErrorFuture<>();

    final Tx tx = transactionConverter.convertToRpcModel(transaction);
    ListenableFuture<VerifyResult> listenableFuture = aergoService.verifyTX(tx);
    FutureChainer<VerifyResult, Boolean> callback =
        new FutureChainer<>(nextFuture, verifyResult -> 0 == verifyResult.getErrorValue());
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;
  }

  @Override
  public ResultOrErrorFuture<Optional<Hash>> commit(final Transaction transaction) {
    ResultOrErrorFuture<Optional<Hash>> nextFuture = new ResultOrErrorFuture<>();

    final Tx tx = transactionConverter.convertToRpcModel(transaction);
    final TxList txList = TxList.newBuilder().addTxs(tx).build();
    ListenableFuture<CommitResultList> listenableFuture = aergoService.commitTX(txList);
    FutureChainer<CommitResultList, Optional<Hash>> callback = new FutureChainer<>(nextFuture,
        commitResultList -> commitResultList.getResultsList().stream()
            .filter(r -> r.getError() == CommitStatus.COMMIT_STATUS_OK)
            .map(r -> r.getHash().toByteArray()).map(Hash::new).findFirst());
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;
  }
}
