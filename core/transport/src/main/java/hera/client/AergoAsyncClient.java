/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.Context;
import hera.ContextHolder;
import hera.StrategyAcceptable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.AbstractAsyncAergoApi;
import hera.api.AccountAsyncOperation;
import hera.api.BlockAsyncOperation;
import hera.api.BlockchainAsyncOperation;
import hera.api.ContractAsyncOperation;
import hera.api.TransactionAsyncOperation;
import hera.strategy.StrategyChain;
import io.grpc.ManagedChannel;
import java.io.Closeable;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@ApiAudience.Private
@ApiStability.Unstable
@RequiredArgsConstructor
public class AergoAsyncClient extends AbstractAsyncAergoApi implements Closeable, AutoCloseable {

  protected final Context globalContext;

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final ManagedChannel channel = new ManagedChannelFactory().apply(ContextHolder.get());

  @Getter(lazy = true)
  private final AccountAsyncOperation accountAsyncOperation =
      resolveInjection(new AccountAsyncTemplate());

  @Getter(lazy = true)
  private final BlockAsyncOperation blockAsyncOperation =
      resolveInjection(new BlockAsyncTemplate());

  @Getter(lazy = true)
  private final BlockchainAsyncOperation blockchainAsyncOperation =
      resolveInjection(new BlockchainAsyncTemplate());

  @Getter(lazy = true)
  private final TransactionAsyncOperation transactionAsyncOperation =
      resolveInjection(new TransactionAsyncTemplate());

  @Getter(lazy = true)
  private final ContractAsyncOperation contractAsyncOperation =
      resolveInjection(new ContractAsyncTemplate());

  protected <T> T resolveInjection(final T target) {
    if (target instanceof StrategyAcceptable) {
      ((StrategyAcceptable) target).accept(StrategyChain.of(ContextHolder.get()));
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
