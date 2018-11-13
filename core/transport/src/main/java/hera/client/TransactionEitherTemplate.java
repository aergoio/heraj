/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.Context;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.TransactionEitherOperation;
import hera.api.model.AccountAddress;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.tupleorerror.ResultOrError;
import hera.exception.NoStrategyFoundException;
import hera.exception.RpcException;
import hera.strategy.TimeoutStrategy;
import io.grpc.ManagedChannel;
import io.opentracing.Scope;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

@ApiAudience.Private
@ApiStability.Unstable
public class TransactionEitherTemplate implements TransactionEitherOperation, ChannelInjectable {

  protected Context context;

  protected TransactionAsyncTemplate transactionAsyncOperation = new TransactionAsyncTemplate();

  @Override
  public void setContext(final Context context) {
    this.context = context;
    transactionAsyncOperation.setContext(context);
  }

  @Override
  public void injectChannel(final ManagedChannel channel) {
    transactionAsyncOperation.injectChannel(channel);
  }

  @Override
  public ResultOrError<Transaction> getTransaction(final TxHash txHash) {
    return context.getStrategy(TimeoutStrategy.class)
        .map(f -> f.submit(transactionAsyncOperation.getTransaction(txHash)))
        .orElseThrow(() -> new RpcException(new NoStrategyFoundException(TimeoutStrategy.class)));
  }

  @Override
  public ResultOrError<TxHash> commit(final Transaction transaction) {
    final Tracer tracer = GlobalTracer.get();
    try (final Scope ignored = tracer.buildSpan("heraj.committx.either").startActive(true)) {
      return context.getStrategy(TimeoutStrategy.class)
        .map(f -> f.submit(transactionAsyncOperation.commit(transaction)))
        .orElseThrow(() -> new RpcException(new NoStrategyFoundException(TimeoutStrategy.class)));
    }
  }

  @Override
  public ResultOrError<TxHash> send(final AccountAddress sender, final AccountAddress recipient,
      final long amount) {
    final Tracer tracer = GlobalTracer.get();
    try (final Scope ignored = tracer.buildSpan("heraj.sendtx.either").startActive(true)) {
      return context.getStrategy(TimeoutStrategy.class)
        .map(f -> f.submit(transactionAsyncOperation.send(sender, recipient, amount)))
        .orElseThrow(() -> new RpcException(new NoStrategyFoundException(TimeoutStrategy.class)));
    }
  }

}
