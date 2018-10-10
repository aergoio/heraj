/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import org.junit.Test;

public class Sha256UtilsTest {

  protected static final String HEXA_ENCODED =
      "09CA7E4EAA6E8AE9C7D261167129184883644D07DFBA7CBFBC4C8A2E08360D5B";

  @Test
  public void testGetBytes() throws Exception {
    final String characterSequence = randomUUID().toString();
    final byte[] expected = characterSequence.getBytes("utf-8");
    assertTrue(Arrays.equals(expected, Sha256Utils.getBytes(characterSequence)));
  }

  @Test
  public void testRaw() {
    final String raw = "hello, world";
    final byte[] encoded = Sha256Utils.digest(raw.getBytes());
    assertEquals(HEXA_ENCODED, HexUtils.encode(encoded));
  }

  @Test
  public void testRaws() {
    final String raw1 = "hello, ";
    final String raw2 = "world";
    final byte[] encoded = Sha256Utils.digest(raw1.getBytes(), raw2.getBytes());
    assertEquals(HEXA_ENCODED, HexUtils.encode(encoded));
  }

  @Test
  public void testMask() throws Exception {
    assertNull(Sha256Utils.mask(null));
    assertTrue(Sha256Utils.mask(randomUUID().toString()).contains("**"));
  }
}
