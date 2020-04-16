/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.HostnameAndPort;
import io.grpc.ManagedChannel;
import org.junit.Test;

public class NettyConnectStrategyTest extends AbstractTestCase {

  @Test
  public void testConnect() throws InterruptedException {
    final NettyConnectStrategy connectStrategy = new NettyConnectStrategy();
    final ManagedChannel channel = connectStrategy.connect(HostnameAndPort.of("localhost:9999"))
        .build();
    assertNotNull(channel);
    channel.shutdown().awaitTermination(1, SECONDS);
  }
}
