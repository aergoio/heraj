/*
 * @copyright defined in LICENSE.txt
 */

package hera.key;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.KeyFormat;
import java.io.IOException;
import org.junit.Test;

public class KeyFileV1StrategyTest extends AbstractTestCase {

  @Test
  public void testDecrypt() throws IOException {
    final KeyFormatV1Strategy strategy = new KeyFormatV1Strategy();
    final KeyFormat keyFile = KeyFormat.of(open("keystore"));
    final AergoKey decrypted = strategy.decrypt(keyFile, "password");
    assertNotNull(decrypted);
  }

  @Test
  public void testEncryptAndDecrypt() throws Exception {
    final KeyFormatV1Strategy strategy = new KeyFormatV1Strategy();
    final AergoKey expected = new AergoKeyGenerator().create();
    final String password = randomUUID().toString();
    final KeyFormat keyFile = strategy.encrypt(expected, password);
    for (int i = 0; i < 10; ++i) {
      final AergoKey actual = strategy.decrypt(keyFile, password);
      assertEquals(expected, actual);
    }
  }

}
