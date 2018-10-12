/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.Context;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.AbstractAsyncAergoApi;
import hera.api.AccountAsyncOperation;
import hera.api.BlockAsyncOperation;
import hera.api.BlockChainAsyncOperation;
import hera.api.ContractAsyncOperation;
import hera.api.SignAsyncOperation;
import hera.api.TransactionAsyncOperation;
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
public class AergoAsyncClient extends AbstractAsyncAergoApi implements Closeable, AutoCloseable {

  @NonNull
  protected final Context context;

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final ManagedChannel channel = (ManagedChannel) context.getStrategy(ConnectStrategy.class)
      .map(ConnectStrategy::connect).get();

  @Getter(lazy = true)
  private final SignAsyncOperation signAsyncOperation =
      context.getStrategy(SignStrategy.class).map(s -> s.getSignOperation(channel, context))
          .flatMap(o -> o.adapt(SignAsyncOperation.class)).get();

  @Getter(lazy = true)
  private final AccountAsyncOperation accountAsyncOperation =
      new AccountAsyncTemplate(getChannel(), context);

  @Getter(lazy = true)
  private final TransactionAsyncOperation transactionAsyncOperation =
      new TransactionAsyncTemplate(getChannel(), context);

  @Getter(lazy = true)
  private final BlockAsyncOperation blockAsyncOperation =
      new BlockAsyncTemplate(getChannel(), context);

  @Getter(lazy = true)
  private final BlockChainAsyncOperation blockChainAsyncOperation =
      new BlockChainAsyncTemplate(getChannel(), context);

  @Getter(lazy = true)
  private final ContractAsyncOperation contractAsyncOperation =
      new ContractAsyncTemplate(getChannel(), context);

  @Override
  public void close() {
    try {
      getChannel().shutdown().awaitTermination(3, TimeUnit.SECONDS);
    } catch (final Throwable e) {
      logger.debug("Fail to close aergo client", e);
    }
  }

}
