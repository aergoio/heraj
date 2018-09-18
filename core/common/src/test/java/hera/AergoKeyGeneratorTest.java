/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static org.junit.Assert.assertNotNull;
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
}