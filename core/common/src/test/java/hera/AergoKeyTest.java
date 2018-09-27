/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class AergoKeyTest extends AbstractTestCase {

  private static final String ENCODED_PRIVATE_KEY = "CSKko9uZtJA1C4kcEECTjgKfswn6dc9aShpsvS73jk9n";

  @Test
  public void testRecover() throws Exception {
    final AergoKey key = AergoKey.recover(ENCODED_PRIVATE_KEY);
    assertNotNull(key.getPrivateKey());
    assertNotNull(key.getPublicKey());
    assertNotNull(key.getAddress());
  }

  @Test
  public void testGetEncodedPrivateKey() throws Exception {
    final AergoKey key = AergoKey.recover(ENCODED_PRIVATE_KEY);
    assertEquals(key.getEncodedPrivateKey(), ENCODED_PRIVATE_KEY);
  }

}
