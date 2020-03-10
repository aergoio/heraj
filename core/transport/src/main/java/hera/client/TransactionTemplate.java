/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static hera.api.function.Functions.identify;
import static hera.client.ClientConstants.TRANSACTION_COMMIT;
import static hera.client.ClientConstants.TRANSACTION_GETTX;

import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.TransactionOperation;
import hera.api.function.Function1;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.client.internal.TransactionBaseTemplate;
import hera.exception.RpcException;
import hera.exception.RpcExceptionConverter;
import hera.strategy.PriorityProvider;
import hera.strategy.StrategyApplier;
import hera.util.ExceptionConverter;
import io.grpc.ManagedChannel;
import java.util.concurrent.Future;
import lombok.AccessLevel;
import lombok.Getter;

@ApiAudience.Private
@ApiStability.Unstable
public class TransactionTemplate
    implements TransactionOperation, ChannelInjectable, ContextProviderInjectable {

  protected final ExceptionConverter<RpcException> exceptionConverter = new RpcExceptionConverter();

  protected TransactionBaseTemplate transactionBaseTemplate = new TransactionBaseTemplate();

  protected ContextProvider contextProvider;

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final StrategyApplier strategyApplier =
      StrategyApplier.of(contextProvider.get(), PriorityProvider.get());

  @Override
  public void setChannel(final ManagedChannel channel) {
    this.transactionBaseTemplate.setChannel(channel);
  }

  @Override
  public void setContextProvider(final ContextProvider contextProvider) {
    this.contextProvider = contextProvider;
    this.transactionBaseTemplate.setContextProvider(contextProvider);
  }

  private final Function1<TxHash, Future<Transaction>> convertedTransactionFunction =
      new Function1<TxHash, Future<Transaction>>() {

        @Override
        public Future<Transaction> apply(final TxHash txHash) {
          final ListenableFuture<Transaction> txInMemory =
              (ListenableFuture<Transaction>) transactionBaseTemplate.getTransactionFunction()
                  .apply(txHash);
          final ListenableFuture<Transaction> future =
              Futures.catching(txInMemory, Throwable.class, new Function<Throwable, Transaction>() {

                @Override
                public Transaction apply(final Throwable input) {
                  try {
                    return transactionBaseTemplate.getTransactionInBlockFunction().apply(txHash)
                        .get();
                  } catch (Exception e) {
                    // TODO: fixme
                    throw new RpcException(e);
                  }
                }
              }, directExecutor());
          return future;
        }
      };

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<TxHash, Future<Transaction>> transactionFunction =
      getStrategyApplier().apply(identify(convertedTransactionFunction,
          TRANSACTION_GETTX));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<Transaction, Future<TxHash>> commitFunction =
      getStrategyApplier()
          .apply(identify(this.transactionBaseTemplate.getCommitFunction(), TRANSACTION_COMMIT));

  @Override
  public Transaction getTransaction(final TxHash txHash) {
    try {
      return getTransactionFunction().apply(txHash).get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public TxHash commit(final Transaction transaction) {
    try {
      return getCommitFunction().apply(transaction).get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

}
