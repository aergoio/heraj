/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static org.slf4j.LoggerFactory.getLogger;

import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;

public abstract class AbstractIT {

  protected final transient Logger logger = getLogger(getClass());

  protected ManagedChannel channel = null;

  @Before
  public void setUp() {
    channel = NettyChannelBuilder
        .forAddress("localhost", 7845)
        .usePlaintext()
        .build();
  }

  @After
  public void tearDown() {
    channel.shutdown();
  }

  protected void waitForNextBlockToGenerate() throws InterruptedException {
    Thread.sleep(2500L);
  }

}
