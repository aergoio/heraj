/*
 * @copyright defined in LICENSE.txt
 */

package hera.transaction;

import static org.junit.Assert.assertEquals;

import hera.AbstractTestCase;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import org.junit.Test;

public class SimpleNonceProviderTest extends AbstractTestCase {

  @Test
  public void testLimitCapacity() {
    final int capacity = 3;
    final SimpleNonceProvider nonceProvider = new SimpleNonceProvider(capacity);

    for (int i = 0; i < capacity + 1; ++i) {
      final AergoKey key = new AergoKeyGenerator().create();
      nonceProvider.bindNonce(key.getAddress(), 3L);
    }

    assertEquals(capacity, nonceProvider.address2Nonce.size());
  }


  @Test
  public void testIncrementAndGetNonce() {
    final NonceProvider nonceProvider = new SimpleNonceProvider();
    final AergoKey key = new AergoKeyGenerator().create();
    final int tryCount = 10;
    for (int i = 0; i < tryCount; ++i) {
      nonceProvider.incrementAndGetNonce(key.getAddress());
    }
    assertEquals(tryCount + 1, nonceProvider.incrementAndGetNonce(key.getAddress()));
  }

}
