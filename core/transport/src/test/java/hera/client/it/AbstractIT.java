/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import static hera.DefaultConstants.DEFAULT_ENDPOINT;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.HostnameAndPort;
import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;
import java.io.InputStream;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;

public abstract class AbstractIT {

  protected final transient Logger logger = getLogger(getClass());

  protected ManagedChannel channel = null;

  protected InputStream open(final String ext) {
    final String path = "/" + getClass().getName().replace('.', '/') + "." + ext;
    logger.trace("Path: {}", path);
    return getClass().getResourceAsStream(path);
  }

  @Before
  public void setUp() {
    final HostnameAndPort hostnameAndPort = HostnameAndPort.of(DEFAULT_ENDPOINT);
    channel = NettyChannelBuilder
        .forAddress(hostnameAndPort.getHostname(), hostnameAndPort.getPort())
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
