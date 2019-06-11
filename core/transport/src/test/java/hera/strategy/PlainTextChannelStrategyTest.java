/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;


import static java.util.UUID.randomUUID;

import hera.AbstractTestCase;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import org.junit.Test;

public class PlainTextChannelStrategyTest extends AbstractTestCase {

  @Test
  public void testConnect() {
    final PlainTextChannelStrategy strategy = new PlainTextChannelStrategy();
    final NettyChannelBuilder channelBuilder =
        NettyChannelBuilder.forTarget(randomUUID().toString());
    strategy.configure(channelBuilder);
  }

}
