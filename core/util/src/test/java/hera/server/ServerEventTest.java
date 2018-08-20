/*
 * @copyright defined in LICENSE.txt
 */

package hera.server;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import org.junit.Test;

public class ServerEventTest extends AbstractTestCase {

  @Test
  public void
  testToString() {
    final ServerEvent event = new ServerEvent(null, 0);
    assertNotNull(event.toString());

  }

}
