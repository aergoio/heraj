/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.api.AbstractAergoApi;
import hera.api.AccountOperation;
import hera.api.BlockChainOperation;
import hera.api.BlockOperation;
import hera.api.ContractOperation;
import hera.api.SignOperation;
import hera.api.TransactionOperation;
import io.grpc.ManagedChannel;
import java.io.Closeable;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AergoClient extends AbstractAergoApi implements Closeable, AutoCloseable {

  @NonNull
  protected final ManagedChannel channel;

  @Getter(lazy = true)
  private final SignOperation signOperation = new SignTemplate(channel);

  @Getter(lazy = true)
  private final AccountOperation accountOperation = new AccountTemplate(channel);

  @Getter(lazy = true)
  private final TransactionOperation transactionOperation = new TransactionTemplate(channel);

  @Getter(lazy = true)
  private final BlockOperation blockOperation = new BlockTemplate(channel);

  @Getter(lazy = true)
  private final BlockChainOperation blockChainOperation = new BlockChainTemplate(channel);

  @Getter(lazy = true)
  private final ContractOperation contractOperation = new ContractTemplate(channel);

  @Override
  public void close() {
    try {
      channel.shutdown().awaitTermination(3, TimeUnit.SECONDS);
    } catch (final Throwable e) {
      logger.debug("Fail to close aergo client", e);
    }
  }
}
