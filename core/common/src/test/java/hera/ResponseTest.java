/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class ResponseTest extends AbstractTestCase {

  @Test
  public void testSuccess() {
    final String expected = randomUUID().toString();
    final Response<String> response = Response.success(expected);
    assertEquals(expected, response.getValue());
    assertNull(response.getError());
  }

  @Test
  public void testFail() {
    final Exception expected = new UnsupportedOperationException();
    final Response<String> response = Response.fail(expected);
    assertNull(response.getValue());
    assertEquals(expected, response.getError());
  }

}
