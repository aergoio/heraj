/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.model.BytesValue.of;
import static hera.util.TransportUtils.copyFrom;
import static java.util.Optional.ofNullable;
import static org.slf4j.LoggerFactory.getLogger;
import static types.AergoRPCServiceGrpc.newFutureStub;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.ByteString;
import hera.Context;
import hera.FutureChainer;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.TransactionAsyncOperation;
import hera.api.model.AccountAddress;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.exception.CommitException;
import hera.transport.ModelConverter;
import hera.transport.TransactionConverterFactory;
import hera.transport.TransactionInBlockConverterFactory;
import io.grpc.ManagedChannel;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain;
import types.Blockchain.Tx;
import types.Blockchain.TxInBlock;
import types.Blockchain.TxList;
import types.Rpc;
import types.Rpc.CommitResultList;
import types.Rpc.SingleBytes;

@ApiAudience.Private
@ApiStability.Unstable
@SuppressWarnings("unchecked")
public class TransactionAsyncTemplate implements TransactionAsyncOperation, ChannelInjectable {

  protected final Logger logger = getLogger(getClass());

  protected final ModelConverter<Transaction, Blockchain.Tx> transactionConverter =
      new TransactionConverterFactory().create();

  protected final ModelConverter<Transaction, Blockchain.TxInBlock> transactionInBlockConverter =
      new TransactionInBlockConverterFactory().create();

  @Setter
  protected Context context;

  @Getter
  protected AergoRPCServiceFutureStub aergoService;

  @Override
  public void injectChannel(final ManagedChannel channel) {
    this.aergoService = newFutureStub(channel);
  }

  @Override
  public ResultOrErrorFuture<Transaction> getTransaction(final TxHash txHash) {
    ResultOrErrorFuture<Transaction> nextFuture = ResultOrErrorFutureFactory.supplyEmptyFuture();

    final ByteString byteString = copyFrom(txHash.getBytesValue());
    final SingleBytes hashBytes = SingleBytes.newBuilder().setValue(byteString).build();
    ListenableFuture<TxInBlock> listenableFuture = aergoService.getBlockTX(hashBytes);
    FutureChainer<TxInBlock, Transaction> callback = new FutureChainer<TxInBlock, Transaction>(
        nextFuture, txInBlock -> transactionInBlockConverter.convertToDomainModel(txInBlock)) {
      @Override
      public void onFailure(Throwable throwable) {
        try {
          logger.debug("Transaction {} is not in a block. Check mempool", txHash);
          final Blockchain.Tx tx = aergoService.getTX(hashBytes).get();
          super.onSuccess(TxInBlock.newBuilder().setTx(tx).build());
        } catch (Throwable e) {
          logger.debug("Transaction {} don't exist", txHash);
          super.onFailure(e);
        }
      }
    };
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;
  }

  @Override
  public ResultOrErrorFuture<TxHash> commit(final Transaction transaction) {
    ResultOrErrorFuture<TxHash> nextFuture = ResultOrErrorFutureFactory.supplyEmptyFuture();

    final Tx tx = transactionConverter.convertToRpcModel(transaction);
    final TxList txList = TxList.newBuilder().addTxs(tx).build();
    ListenableFuture<CommitResultList> listenableFuture = aergoService.commitTX(txList);
    FutureChainer<CommitResultList, TxHash> callback = new FutureChainer<CommitResultList, TxHash>(
        nextFuture, commitResultList -> commitResultList.getResultsList().stream()
            .map(r -> r.getHash().toByteArray()).map(b -> new TxHash(of(b))).findFirst().get()) {
      @Override
      public void onSuccess(CommitResultList t) {
        final Rpc.CommitResult commitResult = t.getResults(0);
        if (Rpc.CommitStatus.TX_OK == commitResult.getError()) {
          super.onSuccess(t);
        } else {
          super.onFailure(new CommitException(commitResult.getError()));
        }
      }
    };
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;
  }

  @Override
  public ResultOrErrorFuture<TxHash> send(final AccountAddress sender,
      final AccountAddress recipient, final long amount) {
    ResultOrErrorFuture<TxHash> nextFuture = ResultOrErrorFutureFactory.supplyEmptyFuture();

    final Transaction transaction = new Transaction();
    transaction.setSender(sender);
    transaction.setRecipient(recipient);
    transaction.setAmount(amount);
    final Tx tx = transactionConverter.convertToRpcModel(transaction);
    ListenableFuture<Rpc.CommitResult> listenableFuture = aergoService.sendTX(tx);
    FutureChainer<Rpc.CommitResult, TxHash> callback =
        new FutureChainer<Rpc.CommitResult, TxHash>(nextFuture,
            c -> ofNullable(c).filter(v -> Rpc.CommitStatus.TX_OK == v.getError())
                .map(v -> v.getHash().toByteArray()).map(b -> new TxHash(of(b)))
                .orElseThrow(() -> new CommitException(c.getError())));
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;
  }

}
