/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.Context;
import hera.ContextAware;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.AbstractAsyncAergoApi;
import hera.api.AccountAsyncOperation;
import hera.api.BlockAsyncOperation;
import hera.api.BlockChainAsyncOperation;
import hera.api.ContractAsyncOperation;
import hera.api.TransactionAsyncOperation;
import hera.strategy.ConnectStrategy;
import io.grpc.ManagedChannel;
import java.io.Closeable;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@ApiAudience.Private
@ApiStability.Unstable
@RequiredArgsConstructor
public class AergoAsyncClient extends AbstractAsyncAergoApi implements Closeable, AutoCloseable {

  @NonNull
  protected final Context context;

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final ManagedChannel channel = (ManagedChannel) context.getStrategy(ConnectStrategy.class)
      .map(ConnectStrategy::connect).get();

  @Getter(lazy = true)
  private final AccountAsyncOperation accountAsyncOperation =
      resolveInjection(new AccountAsyncTemplate());

  @Getter(lazy = true)
  private final TransactionAsyncOperation transactionAsyncOperation =
      resolveInjection(new TransactionAsyncTemplate());

  @Getter(lazy = true)
  private final BlockAsyncOperation blockAsyncOperation =
      resolveInjection(new BlockAsyncTemplate());

  @Getter(lazy = true)
  private final BlockChainAsyncOperation blockChainAsyncOperation =
      resolveInjection(new BlockChainAsyncTemplate());

  @Getter(lazy = true)
  private final ContractAsyncOperation contractAsyncOperation =
      resolveInjection(new ContractAsyncTemplate());

  protected <T> T resolveInjection(final T target) {
    if (target instanceof ContextAware) {
      ((ContextAware) target).setContext(context);
    }
    if (target instanceof ChannelInjectable) {
      ((ChannelInjectable) target).injectChannel(getChannel());
    }
    return target;
  }

  @Override
  public void close() {
    try {
      getChannel().shutdown().awaitTermination(3, TimeUnit.SECONDS);
    } catch (final Throwable e) {
      logger.debug("Fail to close aergo client", e);
    }
  }

}
