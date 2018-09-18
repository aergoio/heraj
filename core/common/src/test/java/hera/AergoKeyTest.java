/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class AergoKeyTest extends AbstractTestCase {

  @Test
  public void testRecover() throws Exception {
    final AergoKey key = AergoKey.recover("CSKko9uZtJA1C4kcEECTjgKfswn6dc9aShpsvS73jk9n");
    assertNotNull(key.getPrivateKey());
    assertNotNull(key.getPublicKey());
    assertNotNull(key.getAddress());
  }

}