/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import hera.AbstractTestCase;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Test;

public class Base64UtilsTest extends AbstractTestCase {

  public static final byte[] DECODED = "Hello Aergo this is hera the queen".getBytes();

  public static final String ENCODED = "SGVsbG8gQWVyZ28gdGhpcyBpcyBoZXJhIHRoZSBxdWVlbg==";

  @Test
  public void testEncode() {
    assertEquals(ENCODED, Base64Utils.encode(DECODED));
    assertEquals("", Base64Utils.encode(new byte[0]));
    assertEquals("", Base64Utils.encode(null));
  }

  @Test
  public void testDecode() throws IOException {
    assertTrue(Arrays.equals(DECODED, Base64Utils.decode(ENCODED)));
    assertTrue(Arrays.equals(new byte[0], Base64Utils.decode("")));
    assertTrue(Arrays.equals(new byte[0], Base64Utils.decode(null)));
  }

}
