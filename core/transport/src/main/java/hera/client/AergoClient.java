/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.Context;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.AbstractAergoApi;
import hera.api.AccountOperation;
import hera.api.BlockChainOperation;
import hera.api.BlockOperation;
import hera.api.ContractOperation;
import hera.api.SignOperation;
import hera.api.TransactionOperation;
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
public class AergoClient extends AbstractAergoApi implements Closeable, AutoCloseable {

  @NonNull
  protected final Context context;

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final ManagedChannel channel = (ManagedChannel) context.getStrategy(ConnectStrategy.class)
      .map(ConnectStrategy::connect).get();

  @Getter(lazy = true)
  private final SignOperation signOperation = context.getStrategy(SignStrategy.class)
      .map(s -> s.getSignOperation(getChannel(), context)).get();

  @Getter(lazy = true)
  private final AccountOperation accountOperation = new AccountTemplate(getChannel(), context);

  @Getter(lazy = true)
  private final TransactionOperation transactionOperation =
      new TransactionTemplate(getChannel(), context);

  @Getter(lazy = true)
  private final BlockOperation blockOperation = new BlockTemplate(getChannel(), context);

  @Getter(lazy = true)
  private final BlockChainOperation blockChainOperation =
      new BlockChainTemplate(getChannel(), context);

  @Getter(lazy = true)
  private final ContractOperation contractOperation = new ContractTemplate(getChannel(), context);

  @Override
  public void close() {
    try {
      getChannel().shutdown().awaitTermination(3, TimeUnit.SECONDS);
    } catch (final Throwable e) {
      logger.debug("Fail to close aergo client", e);
    }
  }

}
