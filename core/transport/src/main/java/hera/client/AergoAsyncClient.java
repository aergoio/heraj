/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.AbstractAsyncAergoApi;
import hera.api.AccountAsyncOperation;
import hera.api.BlockAsyncOperation;
import hera.api.BlockChainAsyncOperation;
import hera.api.ContractAsyncOperation;
import hera.api.SignAsyncOperation;
import hera.api.TransactionAsyncOperation;
import io.grpc.ManagedChannel;
import java.io.Closeable;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@ApiAudience.Public
@ApiStability.Unstable
@RequiredArgsConstructor
public class AergoAsyncClient extends AbstractAsyncAergoApi implements Closeable, AutoCloseable {

  @NonNull
  protected final ManagedChannel channel;

  @Getter(lazy = true)
  private final SignAsyncOperation signAsyncOperation = new SignAsyncTemplate(channel);

  @Getter(lazy = true)
  private final AccountAsyncOperation accountAsyncOperation = new AccountAsyncTemplate(channel);

  @Getter(lazy = true)
  private final TransactionAsyncOperation transactionAsyncOperation =
      new TransactionAsyncTemplate(channel);

  @Getter(lazy = true)
  private final BlockAsyncOperation blockAsyncOperation = new BlockAsyncTemplate(channel);

  @Getter(lazy = true)
  private final BlockChainAsyncOperation blockChainAsyncOperation =
      new BlockChainAsyncTemplate(channel);

  @Getter(lazy = true)
  private final ContractAsyncOperation contractAsyncOperation = new ContractAsyncTemplate(channel);

  @Override
  public void close() {
    try {
      channel.shutdown().awaitTermination(3, TimeUnit.SECONDS);
    } catch (final Throwable e) {
      logger.debug("Fail to close aergo client", e);
    }
  }
}
