/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import static java.util.concurrent.TimeUnit.SECONDS;

import hera.AbstractTestCase;
import hera.EmptyContext;
import io.grpc.ManagedChannel;
import org.junit.Test;

public class OkHttpConnectStrategyTest extends AbstractTestCase {

  @Test
  public void testConnect() throws InterruptedException {
    final OkHttpConnectStrategy connectStrategy = new OkHttpConnectStrategy();
    connectStrategy
        .setContext(EmptyContext.getInstance().withKeyValue("endpoint", "localhost:7845"));
    final ManagedChannel channel = connectStrategy.connect().build();

    channel.shutdown().awaitTermination(1, SECONDS);
  }
}
