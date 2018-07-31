/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class Sha256UtilsTest {

  @Test
  public void test() {
    final String raw = "hello, world";
    final byte[] encoded = Sha256Utils.digest(raw.getBytes());
    assertNotNull(encoded);
  }

  @Test
  public void testMask() throws Exception {
    assertNull(Sha256Utils.mask(null));
    assertTrue(Sha256Utils.mask(randomUUID().toString()).contains("**"));
  }
}
