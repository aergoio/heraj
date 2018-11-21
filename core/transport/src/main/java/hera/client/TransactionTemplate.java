/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.StrategyAcceptable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.TransactionOperation;
import hera.api.model.AccountAddress;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.strategy.StrategyChain;
import io.grpc.ManagedChannel;
import io.opentracing.Scope;
import io.opentracing.util.GlobalTracer;

@ApiAudience.Private
@ApiStability.Unstable
public class TransactionTemplate
    implements TransactionOperation, ChannelInjectable, StrategyAcceptable {

  protected TransactionEitherTemplate transactionEitherOperation = new TransactionEitherTemplate();

  @Override
  public void injectChannel(final ManagedChannel channel) {
    transactionEitherOperation.injectChannel(channel);
  }

  @Override
  public void accept(final StrategyChain strategyChain) {
    transactionEitherOperation.accept(strategyChain);
  }

  @Override
  public Transaction getTransaction(final TxHash txHash) {
    return transactionEitherOperation.getTransaction(txHash).getResult();
  }

  @Override
  public TxHash commit(final Transaction transaction) {
    try (final Scope ignored = GlobalTracer.get().buildSpan("heraj.committx").startActive(true)) {
      return transactionEitherOperation.commit(transaction).getResult();
    }
  }

  @Override
  public TxHash send(final AccountAddress sender, final AccountAddress recipient,
      final long amount) {
    try (final Scope ignored = GlobalTracer.get().buildSpan("heraj.sendtx").startActive(true)) {
      return transactionEitherOperation.send(sender, recipient, amount).getResult();
    }
  }

}
