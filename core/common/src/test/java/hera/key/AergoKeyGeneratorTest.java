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
  public void testCreateWithSeed() throws Exception {
    final AergoKeyGenerator generator = new AergoKeyGenerator();

    final String seed = randomUUID().toString();
    AergoKey prev = generator.create(seed);
    for (int i = 1; i < 100; ++i) {
      final AergoKey curr = generator.create(seed);
      assertEquals(curr, prev);
      prev = curr;
    }
  }
}
