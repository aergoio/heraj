/*
 * @copyright defined in LICENSE.txt
 */

package hera.util.pki;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import hera.AbstractTestCase;
import hera.util.Base58Utils;
import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import org.junit.Test;

public class ECDSAKeyTest extends AbstractTestCase {

  /* 76860113961741997882252494935617441283670103007926986413066244023125861537337 */
  /* A9ED486A7E8A299C36771B6F3B6B87B2478BCC5ADE248D9FE433FDA1CFDD1639 in hexa */
  protected static final String PRIVATE_KEY = "CSKko9uZtJA1C4kcEECTjgKfswn6dc9aShpsvS73jk9n";

  protected static final String MESSAGE = "BhtkwAaxoHmjGCLp5GncvCR6AggBxRB4iRxTMGJbrVYz";

  @Test
  public void testSign() throws Exception {
    final ECDSAKey key =
        new ECDSAKeyGenerator().create(new BigInteger(1, Base58Utils.decode(PRIVATE_KEY)));
    final byte[] message = Base58Utils.decode(MESSAGE);
    final ECDSASignature expected = new ECDSASignature(
        new BigInteger(
            "112903116466171247254957852742074885675578841047850429300671823964741881940578"),
        new BigInteger(
            "48378612163565051950304976934001209911695304131357129876678389874012900275807"));
    final ECDSASignature actual = key.sign(new ByteArrayInputStream(message));
    assertEquals(expected, actual);
  }

  @Test
  public void testVerify() throws Exception {
    final ECDSAKey key =
        new ECDSAKeyGenerator().create(new BigInteger(1, Base58Utils.decode(PRIVATE_KEY)));
    final byte[] message = Base58Utils.decode(MESSAGE);
    final ECDSASignature signature = new ECDSASignature(
        new BigInteger(
            "112903116466171247254957852742074885675578841047850429300671823964741881940578"),
        new BigInteger(
            "48378612163565051950304976934001209911695304131357129876678389874012900275807"));
    assertTrue(key.verify(new ByteArrayInputStream(message), signature));
  }

  @Test
  public void testSignAndVerify() throws Exception {
    for (int i = 0; i < N_TEST; ++i) {
      final ECDSAKey key = new ECDSAKeyGenerator().create();
      final String plainText = randomUUID().toString();
      logger.debug("Plain text: {}", plainText);
      final ECDSASignature signature = key.sign(new ByteArrayInputStream(plainText.getBytes()));
      assertTrue(key.verify(new ByteArrayInputStream(plainText.getBytes()), signature));
    }
  }

}
