/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.TransactionEitherOperation;
import hera.api.model.AccountAddress;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.tupleorerror.ResultOrError;
import io.grpc.ManagedChannel;
import io.opentracing.Scope;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

@ApiAudience.Private
@ApiStability.Unstable
public class TransactionEitherTemplate
    implements TransactionEitherOperation, ChannelInjectable, ContextProviderInjectable {

  protected TransactionAsyncTemplate transactionAsyncOperation = new TransactionAsyncTemplate();

  protected ContextProvider contextProvider;

  @Override
  public void setChannel(final ManagedChannel channel) {
    transactionAsyncOperation.setChannel(channel);
  }

  @Override
  public void setContextProvider(final ContextProvider contextProvider) {
    this.contextProvider = contextProvider;
    transactionAsyncOperation.setContextProvider(contextProvider);
  }

  @Override
  public ResultOrError<Transaction> getTransaction(final TxHash txHash) {
    return transactionAsyncOperation.getTransaction(txHash).get();
  }

  @Override
  public ResultOrError<TxHash> commit(final Transaction transaction) {
    final Tracer tracer = GlobalTracer.get();
    try (final Scope ignored = tracer.buildSpan("heraj.committx.either").startActive(true)) {
      return transactionAsyncOperation.commit(transaction).get();
    }
  }

  @Override
  public ResultOrError<TxHash> send(final AccountAddress sender, final AccountAddress recipient,
      final long amount) {
    final Tracer tracer = GlobalTracer.get();
    try (final Scope ignored = tracer.buildSpan("heraj.sendtx.either").startActive(true)) {
      return transactionAsyncOperation.send(sender, recipient, amount).get();
    }
  }

}
