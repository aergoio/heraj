/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.internal;

import static com.google.common.util.concurrent.Futures.addCallback;
import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static hera.api.model.BytesValue.of;
import static hera.util.TransportUtils.copyFrom;
import static org.slf4j.LoggerFactory.getLogger;
import static types.AergoRPCServiceGrpc.newFutureStub;

import com.google.common.util.concurrent.ListenableFuture;
import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.function.Function1;
import hera.api.function.Function3;
import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.client.ChannelInjectable;
import hera.exception.RpcCommitException;
import hera.exception.RpcException;
import hera.transport.ModelConverter;
import hera.transport.TransactionConverterFactory;
import hera.transport.TransactionInBlockConverterFactory;
import io.grpc.ManagedChannel;
import lombok.Getter;
import org.slf4j.Logger;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain;
import types.Rpc;

@ApiAudience.Private
@ApiStability.Unstable
public class TransactionBaseTemplate implements ChannelInjectable, ContextProviderInjectable {

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<Transaction, Blockchain.Tx> transactionConverter =
      new TransactionConverterFactory().create();

  protected final ModelConverter<Transaction, Blockchain.TxInBlock> transactionInBlockConverter =
      new TransactionInBlockConverterFactory().create();

  @Getter
  protected AergoRPCServiceFutureStub aergoService;

  protected ContextProvider contextProvider;

  @Override
  public void setChannel(final ManagedChannel channel) {
    this.aergoService = newFutureStub(channel);
  }

  @Override
  public void setContextProvider(final ContextProvider contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Getter
  private final Function1<TxHash, FinishableFuture<Transaction>> transactionFunction =
      new Function1<TxHash, FinishableFuture<Transaction>>() {

        @Override
        public FinishableFuture<Transaction> apply(final TxHash txHash) {
          logger.debug("Get transaction with txHash: {}", txHash);

          FinishableFuture<Transaction> nextFuture = new FinishableFuture<Transaction>();
          try {
            final Rpc.SingleBytes rpcTxHash = Rpc.SingleBytes.newBuilder()
                .setValue(copyFrom(txHash.getBytesValue()))
                .build();
            logger.trace("AergoService getTX arg: {}", rpcTxHash);

            ListenableFuture<Blockchain.Tx> listenableFuture =
                aergoService.getTX(rpcTxHash);
            FutureChain<Blockchain.Tx, Transaction> callback = new FutureChain<>(nextFuture);
            callback.setSuccessHandler(new Function1<Blockchain.Tx, Transaction>() {

              @Override
              public Transaction apply(final Blockchain.Tx rpcTx) {
                return transactionConverter.convertToDomainModel(rpcTx);
              }
            });
            callback.setFailureHandler(new Function1<Throwable, Transaction>() {

              @Override
              public Transaction apply(final Throwable t) {
                logger.debug("Transaction {} is not in a mempool. Check block", txHash);
                try {
                  logger.trace("AergoService getBlockTX arg: {}", rpcTxHash);
                  Blockchain.TxInBlock tx = aergoService.getBlockTX(rpcTxHash).get();
                  return transactionInBlockConverter.convertToDomainModel(tx);
                } catch (Exception e) {
                  throw new RpcException(e);
                }
              }
            });
            addCallback(listenableFuture, callback, directExecutor());
          } catch (Exception e) {
            nextFuture.fail(e);
          }
          return nextFuture;
        }
      };

  @Getter
  private final Function1<Transaction, FinishableFuture<TxHash>> commitFunction =
      new Function1<Transaction, FinishableFuture<TxHash>>() {

        @Override
        public FinishableFuture<TxHash> apply(final Transaction transaction) {
          logger.debug("Commit transaction with signedTx: {}", transaction);

          FinishableFuture<TxHash> nextFuture = new FinishableFuture<TxHash>();
          try {
            final Blockchain.Tx rpcTx = transactionConverter.convertToRpcModel(transaction);
            final Blockchain.TxList rpcTxList =
                Blockchain.TxList.newBuilder().addTxs(rpcTx).build();
            logger.trace("AergoService commitTX arg: {}", rpcTxList);

            ListenableFuture<Rpc.CommitResultList> listenableFuture =
                aergoService.commitTX(rpcTxList);
            FutureChain<Rpc.CommitResultList, TxHash> callback = new FutureChain<>(nextFuture);
            callback.setSuccessHandler(new Function1<Rpc.CommitResultList, TxHash>() {

              @Override
              public TxHash apply(final Rpc.CommitResultList commitResultList) {
                final Rpc.CommitResult commitResult = commitResultList.getResultsList().get(0);
                if (Rpc.CommitStatus.TX_OK != commitResult.getError()) {
                  throw new RpcCommitException(commitResult.getError(), commitResult.getDetail());
                }
                return new TxHash(of(commitResult.getHash().toByteArray()));
              }
            });
            addCallback(listenableFuture, callback, directExecutor());
          } catch (Exception e) {
            nextFuture.fail(e);
          }

          return nextFuture;
        }
      };

  @Getter
  private final Function3<AccountAddress, AccountAddress, Aer,
      FinishableFuture<TxHash>> sendFunction = new Function3<
          AccountAddress, AccountAddress, Aer, FinishableFuture<TxHash>>() {

        @Override
        public FinishableFuture<TxHash> apply(final AccountAddress sender,
            final AccountAddress recipient, final Aer amount) {
          logger.debug("Send transaction request with sender: {}, recipient: {}, amount", sender,
              recipient, amount);

          FinishableFuture<TxHash> nextFuture = new FinishableFuture<TxHash>();
          try {
            final RawTransaction rawTransaction = RawTransaction.newBuilder()
                .chainIdHash(contextProvider.get().getChainIdHash())
                .from(sender)
                .to(recipient)
                .amount(amount)
                .nonce(0L)
                .build();
            final Transaction transaction = Transaction.newBuilder()
                .rawTransaction(rawTransaction)
                .build();

            final Blockchain.Tx rpcTx = transactionConverter.convertToRpcModel(transaction);
            logger.trace("AergoService sendTX arg: {}", rpcTx);

            ListenableFuture<Rpc.CommitResult> listenableFuture = aergoService.sendTX(rpcTx);
            FutureChain<Rpc.CommitResult, TxHash> callback = new FutureChain<>(nextFuture);
            callback.setSuccessHandler(new Function1<Rpc.CommitResult, TxHash>() {

              @Override
              public TxHash apply(final Rpc.CommitResult commitResult) {
                if (Rpc.CommitStatus.TX_OK != commitResult.getError()) {
                  throw new RpcCommitException(commitResult.getError(), commitResult.getDetail());
                }
                return new TxHash(of(commitResult.getHash().toByteArray()));
              }
            });
            addCallback(listenableFuture, callback, directExecutor());
          } catch (Exception e) {
            nextFuture.fail(e);
          }
          return nextFuture;
        }
      };

}
