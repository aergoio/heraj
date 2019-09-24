/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.function.Functions.identify;
import static hera.client.ClientConstants.TRANSACTION_COMMIT;
import static hera.client.ClientConstants.TRANSACTION_GETTX;
import static hera.client.ClientConstants.TRANSACTION_SEND;

import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.TransactionOperation;
import hera.api.function.Function1;
import hera.api.function.Function3;
import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.client.internal.FinishableFuture;
import hera.client.internal.TransactionBaseTemplate;
import hera.strategy.PriorityProvider;
import hera.strategy.StrategyApplier;
import io.grpc.ManagedChannel;
import lombok.AccessLevel;
import lombok.Getter;

@ApiAudience.Private
@ApiStability.Unstable
public class TransactionTemplate
    implements TransactionOperation, ChannelInjectable, ContextProviderInjectable {

  @Getter
  protected TransactionBaseTemplate transactionBaseTemplate = new TransactionBaseTemplate();

  protected ContextProvider contextProvider;

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final StrategyApplier strategyApplier =
      StrategyApplier.of(contextProvider.get(), PriorityProvider.get());

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
  private final Function1<TxHash, FinishableFuture<Transaction>> transactionFunction =
      getStrategyApplier().apply(identify(getTransactionBaseTemplate().getTransactionFunction(),
          TRANSACTION_GETTX));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<Transaction, FinishableFuture<TxHash>> commitFunction =
      getStrategyApplier()
          .apply(identify(getTransactionBaseTemplate().getCommitFunction(), TRANSACTION_COMMIT));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function3<AccountAddress, AccountAddress, Aer,
      FinishableFuture<TxHash>> sendFunction =
          getStrategyApplier()
              .apply(identify(getTransactionBaseTemplate().getSendFunction(), TRANSACTION_SEND));

  @Override
  public Transaction getTransaction(final TxHash txHash) {
    return getTransactionFunction().apply(txHash).get();
  }

  @Override
  public TxHash commit(final Transaction transaction) {
    return getCommitFunction().apply(transaction).get();
  }

  @Override
  public TxHash send(final AccountAddress sender,
      final AccountAddress recipient,
      final Aer amount) {
    return getSendFunction().apply(sender, recipient, amount).get();
  }

}
