/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore.internal;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hera.AbstractTestCase;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import java.io.IOException;
import java.io.InputStreamReader;
import org.junit.Test;

public class KeyStoreV1StrategyTest extends AbstractTestCase {

  @Test
  public void testDecrypt() throws IOException {
    final KeyStoreV1Strategy strategy = new KeyStoreV1Strategy();
    final ObjectMapper mapper = new ObjectMapper();
    final JsonNode jsonNode = mapper.reader().readTree(new InputStreamReader(open("keystore")));
    AergoKey decrypted = strategy.decrypt(jsonNode, "password".toCharArray());
    assertNotNull(decrypted);
  }

  @Test
  public void testEncryptAndDecrypt() throws Exception {
    final KeyStoreV1Strategy strategy = new KeyStoreV1Strategy();
    final AergoKey expected = new AergoKeyGenerator().create();
    final String password = randomUUID().toString();
    final String json = strategy.encrypt(expected, password.toCharArray());
    for (int i = 0; i < 10; ++i) {
      final AergoKey actual = strategy.decrypt(json, password.toCharArray());
      assertEquals(expected, actual);
    }
  }

}
