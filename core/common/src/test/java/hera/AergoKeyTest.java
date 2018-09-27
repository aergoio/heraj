/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import hera.util.HexUtils;
import hera.util.pki.ECDSAKey;
import hera.util.pki.ECDSASignature;
import java.math.BigInteger;
import java.util.Arrays;
import org.junit.Test;

public class AergoKeyTest extends AbstractTestCase {

  private static final String ENCODED_PRIVATE_KEY = "CSKko9uZtJA1C4kcEECTjgKfswn6dc9aShpsvS73jk9n";

  @Test
  public void testRecover() throws Exception {
    final AergoKey key = AergoKey.recover(ENCODED_PRIVATE_KEY);
    assertNotNull(key.getPrivateKey());
    assertNotNull(key.getPublicKey());
    assertNotNull(key.getAddress());
  }

  @Test
  public void testGetEncodedPrivateKey() throws Exception {
    final AergoKey key = AergoKey.recover(ENCODED_PRIVATE_KEY);
    assertEquals(key.getEncodedPrivateKey(), ENCODED_PRIVATE_KEY);
  }

  @Test
  public void testSerialize() throws Exception {
    final AergoKey key = new AergoKeyGenerator().create();
    final ECDSASignature signature = ECDSASignature.of(
        new BigInteger(
            "77742016982977049819968937189730099006007209897399569418319639670259283246582"),
        new BigInteger(
            "24080111729304174841921585755879357193051484773881703660717104599905026449822"));
    final byte[] actual = key.serialize(signature);
    assertTrue(Arrays.equals(HexUtils.decode(
        "3045022100ABE06C1B99DE0C51B4790D24EE52674F532D9057744ED9EEF3F61425F9D1BDF60220353CDC395B12ABB6E297085B4D6F1A9DF7783DB66F95A7E0CE28246FC538219E"),
        actual));
  }
}
