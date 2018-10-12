/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.Context;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.AbstractEitherAergoApi;
import hera.api.AccountEitherOperation;
import hera.api.BlockChainEitherOperation;
import hera.api.BlockEitherOperation;
import hera.api.ContractEitherOperation;
import hera.api.SignEitherOperation;
import hera.api.TransactionEitherOperation;
import hera.strategy.ConnectStrategy;
import hera.strategy.SignStrategy;
import io.grpc.ManagedChannel;
import java.io.Closeable;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@ApiAudience.Public
@ApiStability.Unstable
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AergoEitherClient extends AbstractEitherAergoApi implements Closeable, AutoCloseable {

  @NonNull
  protected final Context context;

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final ManagedChannel channel = (ManagedChannel) context.getStrategy(ConnectStrategy.class)
      .map(ConnectStrategy::connect).get();

  @Getter(lazy = true)
  private final SignEitherOperation signEitherOperation =
      context.getStrategy(SignStrategy.class).map(s -> s.getSignOperation(channel, context))
          .flatMap(o -> o.adapt(SignEitherOperation.class)).get();

  @Getter(lazy = true)
  private final AccountEitherOperation accountEitherOperation =
      new AccountEitherTemplate(getChannel(), context);

  @Getter(lazy = true)
  private final TransactionEitherOperation transactionEitherOperation =
      new TransactionEitherTemplate(getChannel(), context);

  @Getter(lazy = true)
  private final BlockEitherOperation blockEitherOperation =
      new BlockEitherTemplate(getChannel(), context);

  @Getter(lazy = true)
  private final BlockChainEitherOperation blockChainEitherOperation =
      new BlockChainEitherTemplate(getChannel(), context);

  @Getter(lazy = true)
  private final ContractEitherOperation contractEitherOperation =
      new ContractEitherTemplate(getChannel(), context);

  @Override
  public void close() {
    try {
      getChannel().shutdown().awaitTermination(3, TimeUnit.SECONDS);
    } catch (final Throwable e) {
      logger.debug("Fail to close aergo client", e);
    }
  }

}
