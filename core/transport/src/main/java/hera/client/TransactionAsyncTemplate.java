/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.TRANSACTION_COMMIT_ASYNC;
import static hera.TransportConstants.TRANSACTION_GETTX_ASYNC;
import static hera.TransportConstants.TRANSACTION_SEND_ASYNC;
import static hera.api.tupleorerror.Functions.identify;

import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.TransactionAsyncOperation;
import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.tupleorerror.Function1;
import hera.api.tupleorerror.Function3;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.strategy.StrategyChain;
import io.grpc.ManagedChannel;
import lombok.AccessLevel;
import lombok.Getter;

@ApiAudience.Private
@ApiStability.Unstable
public class TransactionAsyncTemplate
    implements TransactionAsyncOperation, ChannelInjectable, ContextProviderInjectable {

  @Getter
  protected TransactionBaseTemplate transactionBaseTemplate = new TransactionBaseTemplate();

  protected ContextProvider contextProvider;

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final StrategyChain strategyChain = StrategyChain.of(contextProvider.get());

  @Override
  public void setChannel(final ManagedChannel channel) {
    getTransactionBaseTemplate().setChannel(channel);
  }

  @Override
  public void setContextProvider(final ContextProvider contextProvider) {
    this.contextProvider = contextProvider;
    getTransactionBaseTemplate().setContextProvider(contextProvider);
  }

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<TxHash, ResultOrErrorFuture<Transaction>> transactionFunction =
      getStrategyChain().apply(identify(getTransactionBaseTemplate().getTransactionFunction(),
          TRANSACTION_GETTX_ASYNC));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<Transaction, ResultOrErrorFuture<TxHash>> commitFunction =
      getStrategyChain()
          .apply(
              identify(getTransactionBaseTemplate().getCommitFunction(), TRANSACTION_COMMIT_ASYNC));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function3<AccountAddress, AccountAddress, Aer,
      ResultOrErrorFuture<TxHash>> sendFunction = getStrategyChain()
          .apply(identify(getTransactionBaseTemplate().getSendFunction(), TRANSACTION_SEND_ASYNC));

  @Override
  public ResultOrErrorFuture<Transaction> getTransaction(final TxHash txHash) {
    return getTransactionFunction().apply(txHash);
  }

  @Override
  public ResultOrErrorFuture<TxHash> commit(final Transaction transaction) {
    return getCommitFunction().apply(transaction);
  }

  @Override
  public ResultOrErrorFuture<TxHash> send(final AccountAddress sender,
      final AccountAddress recipient,
      final Aer amount) {
    return getSendFunction().apply(sender, recipient, amount);
  }

}
