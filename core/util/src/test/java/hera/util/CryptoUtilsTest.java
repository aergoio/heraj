/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Random;
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

  @Test
  public void testEncryptToAes256Gcm() throws Exception {
    final Random random = new Random();
    for (int i = 0; i < 100; ++i) {
      byte[] message = randomUUID().toString().getBytes();
      byte[] nonce = Arrays.copyOfRange(randomUUID().toString().getBytes(), 0, 14);
      byte[] password = new byte[32];
      random.nextBytes(password);
      assertNotNull(CryptoUtils.encryptToAesGcm(message, password, nonce));
    }
  }

  @Test
  public void testDecryptToAes256Gcm() throws Exception {
    final Random random = new Random();
    for (int i = 0; i < 100; ++i) {
      byte[] message = randomUUID().toString().getBytes();
      byte[] nonce = Arrays.copyOfRange(randomUUID().toString().getBytes(), 0, 14);
      byte[] password = new byte[32];
      random.nextBytes(password);
      byte[] encrypted = CryptoUtils.encryptToAesGcm(message, password, nonce);
      assertTrue(Arrays.equals(message, CryptoUtils.decryptFromAesGcm(encrypted, password, nonce)));
    }
  }

}
