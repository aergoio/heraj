/*
 * @copyright defined in LICENSE.txt
 */

package hera.build;

import static org.slf4j.LoggerFactory.getLogger;

import org.junit.Test;
import org.slf4j.Logger;

public class MonitorServerTest {

  protected final transient Logger logger = getLogger(getClass());

  @Test
  public void testServer() {
    final WebServer monitorServer = new WebServer();
    monitorServer.setPort(0);
    monitorServer.boot(true);
    logger.info("Server boot");
    monitorServer.down(true);
    logger.info("Server down");
  }
}