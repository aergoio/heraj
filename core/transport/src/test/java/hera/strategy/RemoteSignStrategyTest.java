/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.client.AergoClientBuilder;
import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;
import org.junit.Test;

public class RemoteSignStrategyTest extends AbstractTestCase {

  @Test
  public void testGetOperation() throws InterruptedException {
    final SignStrategy<ManagedChannel> signStrategy = new RemoteSignStrategy();
    final ManagedChannel channel =
        NettyChannelBuilder.forAddress("localhost", 9999).usePlaintext().build();
    assertNotNull(signStrategy.getSignOperation(channel, AergoClientBuilder.getDefaultContext()));
    channel.shutdown().awaitTermination(1, SECONDS);
  }

}
