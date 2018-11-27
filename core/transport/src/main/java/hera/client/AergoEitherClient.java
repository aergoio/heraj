/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.ContextHolder;
import hera.StrategyAcceptable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.AbstractEitherAergoApi;
import hera.api.AccountEitherOperation;
import hera.api.BlockEitherOperation;
import hera.api.BlockchainEitherOperation;
import hera.api.ContractEitherOperation;
import hera.api.TransactionEitherOperation;
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
public class AergoEitherClient extends AbstractEitherAergoApi implements Closeable, AutoCloseable {

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final ManagedChannel channel = new ManagedChannelFactory().apply(ContextHolder.get());

  @Getter(lazy = true)
  private final AccountEitherOperation accountEitherOperation =
      resolveInjection(new AccountEitherTemplate());

  @Getter(lazy = true)
  private final BlockEitherOperation blockEitherOperation =
      resolveInjection(new BlockEitherTemplate());

  @Getter(lazy = true)
  private final BlockchainEitherOperation blockchainEitherOperation =
      resolveInjection(new BlockchainEitherTemplate());

  @Getter(lazy = true)
  private final TransactionEitherOperation transactionEitherOperation =
      resolveInjection(new TransactionEitherTemplate());

  @Getter(lazy = true)
  private final ContractEitherOperation contractEitherOperation =
      resolveInjection(new ContractEitherTemplate());

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
