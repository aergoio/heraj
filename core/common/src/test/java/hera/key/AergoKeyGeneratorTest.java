/*
 * @copyright defined in LICENSE.txt
 */

package hera.key;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import org.junit.Test;

public class AergoKeyGeneratorTest extends AbstractTestCase {

  @Test
  public void testCreate() throws Exception {
    final AergoKeyGenerator generator = new AergoKeyGenerator();
    final AergoKey key = generator.create();
    assertNotNull(key.getPrivateKey());
    assertNotNull(key.getPublicKey());
    assertNotNull(key.getAddress());
  }

  @Test
  public void testImport() throws Exception {
    final AergoKeyGenerator generator = new AergoKeyGenerator();
    final AergoKey expected = generator.create();
    final String password = randomUUID().toString();

    final AergoKey actual = generator.create(expected.export(password), password);
    assertEquals(expected, actual);
  }

}
