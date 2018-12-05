/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.TRANSACTION_COMMIT_EITHER;
import static hera.TransportConstants.TRANSACTION_GETTX_EITHER;
import static hera.TransportConstants.TRANSACTION_SEND_EITHER;
import static hera.api.tupleorerror.Functions.identify;

import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.TransactionEitherOperation;
import hera.api.model.AccountAddress;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.tupleorerror.Function1;
import hera.api.tupleorerror.Function3;
import hera.api.tupleorerror.ResultOrError;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.strategy.StrategyChain;
import io.grpc.ManagedChannel;
import java.math.BigInteger;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@ApiAudience.Private
@ApiStability.Unstable
public class TransactionEitherTemplate
    implements TransactionEitherOperation, ChannelInjectable, ContextProviderInjectable {

  @Getter
  protected TransactionBaseTemplate transactionBaseTemplate = new TransactionBaseTemplate();

  @Setter
  protected ContextProvider contextProvider;

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final StrategyChain strategyChain = StrategyChain.of(contextProvider.get());

  @Override
  public void setChannel(final ManagedChannel channel) {
    transactionBaseTemplate.setChannel(channel);
  }

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<TxHash, ResultOrErrorFuture<Transaction>> transactionFunction =
      getStrategyChain().apply(identify(transactionBaseTemplate.getTransactionFunction(),
          TRANSACTION_GETTX_EITHER));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<Transaction, ResultOrErrorFuture<TxHash>> commitFunction =
      getStrategyChain()
          .apply(identify(transactionBaseTemplate.getCommitFunction(), TRANSACTION_COMMIT_EITHER));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function3<AccountAddress, AccountAddress, BigInteger,
      ResultOrErrorFuture<TxHash>> sendFunction =
          getStrategyChain()
              .apply(identify(transactionBaseTemplate.getSendFunction(), TRANSACTION_SEND_EITHER));

  @Override
  public ResultOrError<Transaction> getTransaction(final TxHash txHash) {
    return getTransactionFunction().apply(txHash).get();
  }

  @Override
  public ResultOrError<TxHash> commit(final Transaction transaction) {
    return getCommitFunction().apply(transaction).get();
  }

  @Override
  public ResultOrError<TxHash> send(final AccountAddress sender,
      final AccountAddress recipient,
      final BigInteger amount) {
    return getSendFunction().apply(sender, recipient, amount).get();
  }

}
