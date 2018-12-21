/*
 * @copyright defined in LICENSE.txt
 */

package hera.key;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import hera.AbstractTestCase;
import hera.api.model.Aer.Unit;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.util.HexUtils;
import hera.util.pki.ECDSASignature;
import java.math.BigInteger;
import java.util.Arrays;
import org.junit.Test;

public class AergoKeyTest extends AbstractTestCase {

  private static final String ENCRYPTED_PRIVATE_KEY =
      "47RHxbUL3DhA1TMHksEPdVrhumcjdXLAB3Hkv61mqkC9M1Wncai5b91q7hpKydfFHKyyVvgKt";

  @Test
  public void testOfWithEncodedEncryptedPrivateKey() throws Exception {
    final String password = "password";
    final AergoKey key = AergoKey.of(ENCRYPTED_PRIVATE_KEY, password);
    assertNotNull(key.getPrivateKey());
    assertNotNull(key.getPublicKey());
    assertNotNull(key.getAddress());
  }

  @Test
  public void testGetEncryptedPrivateKey() throws Exception {
    final String password = "password";
    final AergoKey key = AergoKey.of(ENCRYPTED_PRIVATE_KEY, password);
    final String newEncryptedPrivateKey = key.export(password).toString();
    assertEquals(ENCRYPTED_PRIVATE_KEY, newEncryptedPrivateKey);
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

  @Test
  public void testSignAndVerify() throws Exception {
    for (int i = 0; i < N_TEST; ++i) {
      final AergoKey key = new AergoKeyGenerator().create();

      final RawTransaction rawTransaction = RawTransaction.newBuilder()
          .from(key.getAddress())
          .to(key.getAddress())
          .amount("10000", Unit.AER)
          .nonce(1L)
          .build();
      final Transaction signedTransaction = key.sign(rawTransaction);
      assertTrue(key.verify(signedTransaction));
    }
  }

}
