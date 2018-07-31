/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import static java.util.concurrent.TimeUnit.SECONDS;

import hera.AbstractTestCase;
import hera.api.model.HostnameAndPort;
import io.grpc.ManagedChannel;
import org.junit.Test;

public class NettyConnectStrategyTest extends AbstractTestCase {

  @Test
  public void testConnect() throws InterruptedException {
    final NettyConnectStrategy nettyConnectStrategy = new NettyConnectStrategy();
    final ManagedChannel channel =
        nettyConnectStrategy.connect(HostnameAndPort.of("localhost:9999"));
    channel.shutdown().awaitTermination(1, SECONDS);
  }
}