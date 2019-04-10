/*
 * @copyright defined in LICENSE.txt
 */

package hera.spec.resolver;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;

import hera.AbstractTestCase;
import hera.api.model.BytesValue;
import hera.api.model.EncryptedPrivateKey;
import org.junit.Test;

public class EncryptedPrivateKeyResolverTest extends AbstractTestCase {

  @Test
  public void testEncryptAndDecrypt() throws Exception {
    final BytesValue expected = new BytesValue(randomUUID().toString().getBytes());
    final String password = randomUUID().toString();
    final EncryptedPrivateKey encryptedPrivateKey =
        EncryptedPrivateKeyResolver.encrypt(expected, password);
    final BytesValue actual = EncryptedPrivateKeyResolver.decrypt(encryptedPrivateKey, password);
    assertEquals(expected, actual);
  }

}
