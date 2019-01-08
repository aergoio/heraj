/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static com.google.common.util.concurrent.Futures.addCallback;
import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static hera.api.model.BytesValue.of;
import static hera.util.TransportUtils.copyFrom;
import static org.slf4j.LoggerFactory.getLogger;
import static types.AergoRPCServiceGrpc.newFutureStub;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.ByteString;
import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.function.Function1;
import hera.api.function.Function3;
import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.BytesValue;
import hera.api.model.Fee;
import hera.api.model.Transaction;
import hera.api.model.Transaction.TxType;
import hera.api.model.TxHash;
import hera.exception.CommitException;
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

  protected final Logger logger = getLogger(getClass());

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
          if (logger.isDebugEnabled()) {
            logger.debug("Get transaction with txHash: {}, Context: {}", txHash,
                contextProvider.get());
          }

          FinishableFuture<Transaction> nextFuture = new FinishableFuture<Transaction>();
          try {
            final ByteString byteString = copyFrom(txHash.getBytesValue());
            final Rpc.SingleBytes request =
                Rpc.SingleBytes.newBuilder().setValue(byteString).build();
            ListenableFuture<Blockchain.TxInBlock> listenableFuture =
                aergoService.getBlockTX(request);

            FutureChain<Blockchain.TxInBlock, Transaction> callback =
                new FutureChain<Blockchain.TxInBlock, Transaction>(nextFuture,
                    contextProvider.get());
            callback.setSuccessHandler(new Function1<Blockchain.TxInBlock, Transaction>() {

              @Override
              public Transaction apply(final Blockchain.TxInBlock txInBlock) {
                return transactionInBlockConverter.convertToDomainModel(txInBlock);
              }
            });
            callback.setFailureHandler(new Function1<Throwable, Transaction>() {

              @Override
              public Transaction apply(final Throwable t) {
                if (logger.isDebugEnabled()) {
                  logger.debug("Transaction {} is not in a block. Check mempool", txHash);
                }
                try {
                  Blockchain.Tx tx = aergoService.getTX(request).get();
                  return transactionConverter.convertToDomainModel(tx);
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
          if (logger.isDebugEnabled()) {
            logger.debug("Commit transaction with tx: {}, Context: {}", transaction,
                contextProvider.get());
          }

          FinishableFuture<TxHash> nextFuture = new FinishableFuture<TxHash>();
          try {
            final Blockchain.Tx tx = transactionConverter.convertToRpcModel(transaction);
            final Blockchain.TxList txList = Blockchain.TxList.newBuilder().addTxs(tx).build();
            ListenableFuture<Rpc.CommitResultList> listenableFuture = aergoService.commitTX(txList);

            FutureChain<Rpc.CommitResultList, TxHash> callback =
                new FutureChain<Rpc.CommitResultList, TxHash>(nextFuture, contextProvider.get());
            callback.setSuccessHandler(new Function1<Rpc.CommitResultList, TxHash>() {

              @Override
              public TxHash apply(final Rpc.CommitResultList commitResultList) {
                final Rpc.CommitResult commitResult = commitResultList.getResultsList().get(0);
                logger.debug("Commit result: {}", commitResult.getError());
                if (Rpc.CommitStatus.TX_OK != commitResult.getError()) {
                  throw new CommitException(commitResult.getError(), commitResult.getDetail());
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
          if (logger.isDebugEnabled()) {
            logger.debug(
                "Send transaction request with sender: {}, recipient: {}, amount, Context: {}",
                sender, recipient, amount, contextProvider.get());
          }

          FinishableFuture<TxHash> nextFuture = new FinishableFuture<TxHash>();
          try {
            final Transaction transaction = new Transaction(
                sender, recipient, amount, 0L, Fee.getDefaultFee(), BytesValue.EMPTY,
                TxType.NORMAL,
                null, null, null, 0, false);

            final Blockchain.Tx tx = transactionConverter.convertToRpcModel(transaction);
            ListenableFuture<Rpc.CommitResult> listenableFuture = aergoService.sendTX(tx);

            FutureChain<Rpc.CommitResult, TxHash> callback =
                new FutureChain<Rpc.CommitResult, TxHash>(nextFuture, contextProvider.get());
            callback.setSuccessHandler(new Function1<Rpc.CommitResult, TxHash>() {

              @Override
              public TxHash apply(final Rpc.CommitResult commitResult) {
                if (Rpc.CommitStatus.TX_OK != commitResult.getError()) {
                  throw new CommitException(commitResult.getError(), commitResult.getDetail());
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
