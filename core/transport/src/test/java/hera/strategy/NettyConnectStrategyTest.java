/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import static java.util.concurrent.TimeUnit.SECONDS;

import hera.AbstractTestCase;
import hera.EmptyContext;
import io.grpc.ManagedChannel;
import org.junit.Test;

public class NettyConnectStrategyTest extends AbstractTestCase {

  @Test
  public void testConnect() throws InterruptedException {
    final NettyConnectStrategy connectStrategy = new NettyConnectStrategy();
    connectStrategy
        .setContext(EmptyContext.getInstance().withKeyValue("endpoint", "localhost:7845"));
    final ManagedChannel channel = connectStrategy.connect().build();

    channel.shutdown().awaitTermination(1, SECONDS);
  }
}
