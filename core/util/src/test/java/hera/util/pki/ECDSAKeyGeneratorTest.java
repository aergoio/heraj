/*
 * @copyright defined in LICENSE.txt
 */

package hera.util.pki;

import static hera.util.HexUtils.dump;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.junit.Test;

public class ECDSAKeyGeneratorTest extends AbstractTestCase {

  @Test
  public void testCreate() throws Exception {
    final ECDSAKeyGenerator generator = new ECDSAKeyGenerator();
    final ECDSAKey key = generator.create();
    assertNotNull(key.getPrivateKey());
    assertNotNull(key.getPublicKey());

    final ECDSAKey arrayRecovered = generator.create(key.getPrivateKey().getEncoded());
    logger.debug("Private Key:\n{}", dump(key.getPrivateKey().getEncoded()));
    logger.debug("Recovered private key:\n{}", dump(arrayRecovered.getPrivateKey().getEncoded()));
    assertArrayEquals(key.getPrivateKey().getEncoded(),
        arrayRecovered.getPrivateKey().getEncoded());
    logger.debug("Public Key:\n{}", dump(key.getPublicKey().getEncoded()));
    logger.debug("Recovered public key:\n{}", dump(arrayRecovered.getPublicKey().getEncoded()));
    assertArrayEquals(key.getPublicKey().getEncoded(), arrayRecovered.getPublicKey().getEncoded());

    final ECDSAKey keyRecovered = generator.create(key.getPrivateKey());
    logger.debug("Private Key:\n{}", dump(key.getPrivateKey().getEncoded()));
    logger.debug("Recovered private key:\n{}", dump(keyRecovered.getPrivateKey().getEncoded()));
    assertArrayEquals(key.getPrivateKey().getEncoded(), keyRecovered.getPrivateKey().getEncoded());
    logger.debug("Public Key:\n{}", dump(key.getPublicKey().getEncoded()));
    logger.debug("Recovered public key:\n{}", dump(keyRecovered.getPublicKey().getEncoded()));
    assertArrayEquals(key.getPublicKey().getEncoded(), keyRecovered.getPublicKey().getEncoded());
  }

  @Test
  public void testCreatePrivateKey() throws Exception {
    final ECDSAKeyGenerator generator = new ECDSAKeyGenerator();
    final ECDSAKey key = generator.create();

    final org.bouncycastle.jce.interfaces.ECPrivateKey ecPrivateKey =
        (org.bouncycastle.jce.interfaces.ECPrivateKey) key.getPrivateKey();
    final PrivateKey recovered = generator.createPrivateKey(ecPrivateKey.getD());
    assertEquals(ecPrivateKey.getD(),
        ((org.bouncycastle.jce.interfaces.ECPrivateKey) recovered).getD());
  }

  @Test
  public void testCreatePublicKey() throws Exception {
    final ECDSAKeyGenerator generator = new ECDSAKeyGenerator();
    final ECDSAKey key = generator.create();

    final org.bouncycastle.jce.interfaces.ECPublicKey ecPublicKey =
        (org.bouncycastle.jce.interfaces.ECPublicKey) key.getPublicKey();
    final PublicKey recovered =
        generator.createPublicKey(ecPublicKey.getQ().getXCoord().toBigInteger(),
            ecPublicKey.getQ().getYCoord().toBigInteger());
    assertEquals(ecPublicKey.getQ(),
        ((org.bouncycastle.jce.interfaces.ECPublicKey) recovered).getQ());
  }

}
