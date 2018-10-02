/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.AbstractEitherAergoApi;
import hera.api.AccountEitherOperation;
import hera.api.BlockChainEitherOperation;
import hera.api.BlockEitherOperation;
import hera.api.ContractEitherOperation;
import hera.api.SignEitherOperation;
import hera.api.TransactionEitherOperation;
import io.grpc.ManagedChannel;
import java.io.Closeable;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@ApiAudience.Public
@ApiStability.Unstable
@RequiredArgsConstructor
public class AergoEitherClient extends AbstractEitherAergoApi implements Closeable, AutoCloseable {

  @NonNull
  protected final ManagedChannel channel;

  @Getter(lazy = true)
  private final SignEitherOperation signOperation = new SignEitherTemplate(channel);

  @Getter(lazy = true)
  private final AccountEitherOperation accountOperation = new AccountEitherTemplate(channel);

  @Getter(lazy = true)
  private final TransactionEitherOperation transactionOperation =
      new TransactionEitherTemplate(channel);

  @Getter(lazy = true)
  private final BlockEitherOperation blockOperation = new BlockEitherTemplate(channel);

  @Getter(lazy = true)
  private final BlockChainEitherOperation blockChainOperation =
      new BlockChainEitherTemplate(channel);

  @Getter(lazy = true)
  private final ContractEitherOperation contractOperation = new ContractEitherTemplate(channel);

  @Override
  public void close() {
    try {
      channel.shutdown().awaitTermination(3, TimeUnit.SECONDS);
    } catch (final Throwable e) {
      logger.debug("Fail to close aergo client", e);
    }
  }
}