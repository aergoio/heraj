/*
 * @copyright defined in LICENSE.txt
 */

package hera.key;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;

import hera.AbstractTestCase;
import hera.api.model.EncryptedPrivateKey;
import org.junit.Test;

public class EncryptedPrivateKeyStrategyTest extends AbstractTestCase {

  @Test
  public void testEncryptAndDecrypt() throws Exception {
    final AergoKey expected = new AergoKeyGenerator().create();
    final String passphrase = randomUUID().toString();
    final KeyCipherStrategy<EncryptedPrivateKey> strategy = new EncryptedPrivateKeyStrategy();
    final EncryptedPrivateKey encrypted = strategy.encrypt(expected, passphrase);
    final AergoKey actual = strategy.decrypt(encrypted, passphrase);
    assertEquals(expected, actual);
  }

}
