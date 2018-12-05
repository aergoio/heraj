/*
 * @copyright defined in LICENSE.txt
 */

package hera.util.pki;

import static hera.util.HexUtils.dump;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
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
}
