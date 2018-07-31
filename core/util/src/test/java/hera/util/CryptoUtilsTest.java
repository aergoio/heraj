/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class CryptoUtilsTest {

  @Test
  public void testEncryptToAes128EcbWithBase64() throws Exception {
    for (int i = 0; i < 100; ++i) {
      assertNotNull(CryptoUtils.encryptToAes128EcbWithBase64(randomUUID().toString()));
    }
  }

  @Test
  public void testDecryptFromAes128EcbWithBase64() throws Exception {
    for (int i = 0; i < 100; ++i) {
      final String source = CryptoUtils.encryptToAes128EcbWithBase64(randomUUID().toString());
      assertNotNull(CryptoUtils.decryptFromAes128EcbWithBase64(source));
    }
  }
}
