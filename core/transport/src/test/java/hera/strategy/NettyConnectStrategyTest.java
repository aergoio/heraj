/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import static java.util.concurrent.TimeUnit.SECONDS;

import hera.AbstractTestCase;
import hera.util.Configuration;
import hera.util.conf.InMemoryConfiguration;
import io.grpc.ManagedChannel;
import org.junit.Test;

public class NettyConnectStrategyTest extends AbstractTestCase {

  @Test
  public void testConnect() throws InterruptedException {
    final Configuration configuration = new InMemoryConfiguration();
    configuration.define("endpoint", "localhost:9999");
    final NettyConnectStrategy nettyConnectStrategy = new NettyConnectStrategy();
    nettyConnectStrategy.setConfiguration(configuration);
    final ManagedChannel channel = nettyConnectStrategy.connect();
    channel.shutdown().awaitTermination(1, SECONDS);
  }
}
