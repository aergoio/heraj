/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import org.junit.Test;

public class InvalidVersionExceptionTest extends AbstractTestCase {

  @Test
  public void testCreation() {
    InvalidVersionException exception = new InvalidVersionException((byte) 0x01, (byte) 0x02);
    assertNotNull(exception.expectedVersion);
    assertNotNull(exception.actualVersion);
    logger.debug(exception.getLocalizedMessage());
  }
}
