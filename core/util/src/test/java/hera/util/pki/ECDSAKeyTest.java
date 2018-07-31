/*
 * @copyright defined in LICENSE.txt
 */

package hera.util.pki;

import static hera.util.HexUtils.dump;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.*;

import hera.util.AbstractTestCase;
import java.io.ByteArrayInputStream;
import org.junit.Test;

public class ECDSAKeyTest extends AbstractTestCase {

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
}