/*
 * @copyright defined in LICENSE.txt
 */

package hera.util.pki;

import static hera.util.HexUtils.dump;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import hera.AbstractTestCase;
import hera.util.Base58Utils;
import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.util.Arrays;
import org.junit.Test;

public class ECDSAKeyTest extends AbstractTestCase {

  /* 76860113961741997882252494935617441283670103007926986413066244023125861537337 */
  /* A9ED486A7E8A299C36771B6F3B6B87B2478BCC5ADE248D9FE433FDA1CFDD1639 in hexa */
  protected static final byte[] PRIVATE_KEY =
      Base58Utils.decode("CSKko9uZtJA1C4kcEECTjgKfswn6dc9aShpsvS73jk9n");

  protected static final String MESSAGE = "BhtkwAaxoHmjGCLp5GncvCR6AggBxRB4iRxTMGJbrVYz";

  @Test
  public void testRecovery() throws Exception {
    final ECDSAKey recoveredKey = ECDSAKey.recover(PRIVATE_KEY);

    ECPublicKey ecPublicKey = (ECPublicKey) recoveredKey.getPublicKey();
    ECPrivateKey ecPrivateKey = (ECPrivateKey) recoveredKey.getPrivateKey();

    final BigInteger privatekeyD = new BigInteger(
        "76860113961741997882252494935617441283670103007926986413066244023125861537337");
    final BigInteger publickeyX = new BigInteger(
        "66409208310611858137537694807619612971935110036796750011795675015908117552692");
    final BigInteger publickeyY = new BigInteger(
        "58246437216680112187807305214575249192860131748845949331271713739254817975376");
    assertTrue(privatekeyD.equals(ecPrivateKey.getD()));
    assertTrue(publickeyX.equals(ecPublicKey.getQ().getX().toBigInteger()));
    assertTrue(publickeyY.equals(ecPublicKey.getQ().getY().toBigInteger()));
  }

  @Test
  public void testSign() throws Exception {
    final ECDSAKey key = ECDSAKey.recover(PRIVATE_KEY);
    final byte[] message = Base58Utils.decode(MESSAGE);
    final byte[] expected = Base58Utils.decode(
        "5zTFN6emy1jKwRfR2AvqrVX4v3xukr9xZzvf3LADTVYr6viuW14u41yErh2ytYgz8DkzvwhnC65819H3X1RzTMqG");
    final byte[] actual = key.sign(new ByteArrayInputStream(message));
    assertTrue(Arrays.areEqual(expected, actual));
  }

  @Test
  public void testVerify() throws Exception {
    final ECDSAKey key = ECDSAKey.recover(PRIVATE_KEY);
    final byte[] message = Base58Utils.decode(MESSAGE);
    final byte[] signature = Base58Utils.decode(
        "5zTFN6emy1jKwRfR2AvqrVX4v3xukr9xZzvf3LADTVYr6viuW14u41yErh2ytYgz8DkzvwhnC65819H3X1RzTMqG");
    assertTrue(key.verify(new ByteArrayInputStream(message), signature));
  }

  @Test
  public void testSignAndVerify() throws Exception {
    for (int i = 0; i < N_TEST; ++i) {
      final ECDSAKey key = new ECDSAKeyGenerator().create();
      final String plainText = randomUUID().toString();
      logger.debug("Plain text: {}", plainText);
      final byte[] signature = key.sign(new ByteArrayInputStream(plainText.getBytes()));
      if (logger.isDebugEnabled()) {
        logger.debug("Signed:\n{}", dump(signature));
      }
      assertTrue(key.verify(new ByteArrayInputStream(plainText.getBytes()), signature));
    }
  }

  @Test
  public void testECDSASign() throws Exception {
    final ECDSAKey key = ECDSAKey.recover(PRIVATE_KEY);
    byte[] message = Base58Utils.decode(MESSAGE);
    final ECDSASignature expected = new ECDSASignature(
        new BigInteger(
            "112903116466171247254957852742074885675578841047850429300671823964741881940578"),
        new BigInteger(
            "48378612163565051950304976934001209911695304131357129876678389874012900275807"));
    final ECDSASignature actual = key.sign(((ECPrivateKey) key.getPrivateKey()).getD(), message);
    assertEquals(expected, actual);
  }

}
