/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.api.AbstractAergoApi;
import hera.api.AccountOperation;
import hera.api.BlockOperation;
import hera.api.SignOperation;
import hera.api.SignTemplate;
import hera.api.TransactionOperation;
import io.grpc.ManagedChannel;
import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AergoClient extends AbstractAergoApi implements Closeable, AutoCloseable {

  protected final ManagedChannel channel;

  @Getter(lazy = true)
  private final SignOperation signOperation = new SignTemplate();

  @Getter(lazy = true)
  private final AccountOperation accountOperation = new AccountTemplate(channel);

  @Getter(lazy = true)
  private final TransactionOperation transactionOperation = new TransactionTemplate(channel);

  @Getter(lazy = true)
  private final BlockOperation blockOperation = new BlockTemplate(channel);

  @Override
  public void close() throws IOException {
    try {
      channel.shutdown().awaitTermination(3, TimeUnit.SECONDS);
    } catch (final InterruptedException e) {
      throw new IOException(e);
    }
  }
}
