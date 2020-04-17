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
    final Response<String> response = Response.empty();
    final String expected = randomUUID().toString();
    response.success(expected);
    assertEquals(expected, response.getValue());
    assertNull(response.getError());
  }

  @Test
  public void testFail() {
    final Response<String> response = Response.empty();
    final Exception expected = new UnsupportedOperationException();
    response.fail(expected);
    assertNull(response.getValue());
    assertEquals(expected, response.getError());
  }

}
