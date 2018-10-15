/*
 * @copyright defined in LICENSE.txt
 */

package hera.exception;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import org.junit.Test;

public class AdaptExceptionTest extends AbstractTestCase {

  @Test
  public void testCreation() {
    AdaptException exception = new AdaptException(Object.class, String.class);
    assertNotNull(exception.from);
    assertNotNull(exception.to);
    logger.debug(exception.getLocalizedMessage());
  }
}
