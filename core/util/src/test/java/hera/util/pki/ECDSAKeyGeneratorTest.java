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
    final ECDSAKey recoveredKey = generator.create(key.getPrivateKey().getEncoded());
    logger.debug("Private Key:\n{}", dump(key.getPrivateKey().getEncoded()));
    logger.debug("Recovered private key:\n{}", dump(recoveredKey.getPrivateKey().getEncoded()));
    assertArrayEquals(key.getPrivateKey().getEncoded(), recoveredKey.getPrivateKey().getEncoded());
    logger.debug("Public Key:\n{}", dump(key.getPublicKey().getEncoded()));
    logger.debug("Recovered public key:\n{}", dump(recoveredKey.getPublicKey().getEncoded()));
    assertArrayEquals(key.getPublicKey().getEncoded(), recoveredKey.getPublicKey().getEncoded());
  }
}