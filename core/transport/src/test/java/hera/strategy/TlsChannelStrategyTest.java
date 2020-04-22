/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;


import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.junit.Test;

public class TlsChannelStrategyTest extends AbstractTestCase {

  @Test
  public void testCreate() {
    final String serverName = "test";
    final InputStream serverCert = new ByteArrayInputStream(randomUUID().toString().getBytes());
    final InputStream clientCert = new ByteArrayInputStream(randomUUID().toString().getBytes());
    final InputStream clientKey = new ByteArrayInputStream(randomUUID().toString().getBytes());
    final TlsChannelStrategy strategy = new TlsChannelStrategy(serverName, serverCert, clientCert,
        clientKey);
    assertNotNull(strategy);
  }

}
