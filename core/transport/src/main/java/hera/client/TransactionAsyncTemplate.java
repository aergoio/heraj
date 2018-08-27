/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.util.TransportUtils.copyFrom;
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
import hera.exception.TransactionVerificationException;
import hera.transport.ModelConverter;
import hera.transport.TransactionConverterFactory;
import hera.transport.TransactionInBlockConverterFactory;
import io.grpc.ManagedChannel;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain;
import types.Blockchain.Tx;
import types.Blockchain.TxInBlock;
import types.Blockchain.TxList;
import types.Rpc;
import types.Rpc.CommitResultList;
import types.Rpc.CommitStatus;
import types.Rpc.SingleBytes;
import types.Rpc.VerifyResult;

@RequiredArgsConstructor
public class TransactionAsyncTemplate implements TransactionAsyncOperation {

  protected final Logger logger = getLogger(getClass());

  protected final AergoRPCServiceFutureStub aergoService;

  protected final ModelConverter<Transaction, Blockchain.Tx> transactionConverter;

  protected final ModelConverter<Transaction, Blockchain.TxInBlock> transactionInBlockConverter;

  public TransactionAsyncTemplate(final ManagedChannel channel) {
    this(newFutureStub(channel));
  }

  public TransactionAsyncTemplate(final AergoRPCServiceFutureStub aergoService) {
    this(aergoService, new TransactionConverterFactory().create(),
        new TransactionInBlockConverterFactory().create());
  }

  @Override
  public ResultOrErrorFuture<Transaction> getTransaction(final Hash hash) {
    ResultOrErrorFuture<Transaction> nextFuture = new ResultOrErrorFuture<>();

    final ByteString byteString = copyFrom(hash);
    final SingleBytes hashBytes = SingleBytes.newBuilder().setValue(byteString).build();
    ListenableFuture<TxInBlock> listenableFuture = aergoService.getBlockTX(hashBytes);
    FutureChainer<TxInBlock, Transaction> callback = new FutureChainer<TxInBlock, Transaction>(
        nextFuture, txInBlock -> transactionInBlockConverter.convertToDomainModel(txInBlock)) {
      @Override
      public void onFailure(Throwable throwable) {
        try {
          // if not in a block chain, check memory pool
          final Blockchain.Tx tx = aergoService.getTX(hashBytes).get();
          super.onSuccess(TxInBlock.newBuilder().setTx(tx).build());
        } catch (Throwable e) {
          super.onFailure(e);
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
    FutureChainer<VerifyResult, Boolean> callback = new FutureChainer<VerifyResult, Boolean>(
        nextFuture, verifyResult -> Rpc.VerifyStatus.VERIFY_STATUS_OK == verifyResult.getError()) {
      @Override
      public void onSuccess(VerifyResult t) {
        if (Rpc.VerifyStatus.VERIFY_STATUS_OK == t.getError()) {
          super.onSuccess(t);
        } else {
          super.onFailure(new TransactionVerificationException(t.getError()));
        }
      }
    };
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;
  }

  @Override
  public ResultOrErrorFuture<Hash> commit(final Transaction transaction) {
    ResultOrErrorFuture<Hash> nextFuture = new ResultOrErrorFuture<>();

    final Tx tx = transactionConverter.convertToRpcModel(transaction);
    final TxList txList = TxList.newBuilder().addTxs(tx).build();
    ListenableFuture<CommitResultList> listenableFuture = aergoService.commitTX(txList);
    FutureChainer<CommitResultList, Hash> callback = new FutureChainer<>(nextFuture,
        commitResultList -> commitResultList.getResultsList().stream()
            .filter(r -> r.getError() == CommitStatus.COMMIT_STATUS_OK)
            .map(r -> r.getHash().toByteArray()).map(Hash::new).findFirst().get());
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;
  }
}
