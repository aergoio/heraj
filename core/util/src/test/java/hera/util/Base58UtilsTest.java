/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import hera.AbstractTestCase;
import java.util.Arrays;
import org.junit.Test;

public class Base58UtilsTest extends AbstractTestCase {

  public static final byte[] DECODED = "Hello Aergo this is hera the queen".getBytes();

  public static final String ENCODED = "2dvaTHm2BaTDBPnm6R2thH1wHtyVdfTpBd98uxhdLxeTdNV";

  @Test
  public void testEncode() {
    final String actualEncoded = Base58Utils.encode(DECODED);
    assertEquals(ENCODED, actualEncoded);
  }

  @Test
  public void testDecode() {
    final byte[] actualDecoded = Base58Utils.decode(ENCODED);
    assertTrue(Arrays.equals(DECODED, actualDecoded));
  }
}
