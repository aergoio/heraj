/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import static java.util.concurrent.TimeUnit.SECONDS;

import hera.AbstractTestCase;
import io.grpc.ManagedChannel;
import org.junit.Test;

public class OkHttpConnectStrategyTest extends AbstractTestCase {

  @Test
  public void testConnect() throws InterruptedException {
    final OkHttpConnectStrategy nettyConnectStrategy = new OkHttpConnectStrategy();
    final ManagedChannel channel = nettyConnectStrategy.connect();
    channel.shutdown().awaitTermination(1, SECONDS);
  }
}
