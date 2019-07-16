/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.transaction;

import static org.junit.Assert.assertEquals;

import hera.AbstractTestCase;
import hera.api.model.AccountAddress;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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
    final AccountAddress identity = new AergoKeyGenerator().create().getAddress();

    final int tryCount = 10;
    for (int i = 0; i < tryCount; ++i) {
      nonceProvider.incrementAndGetNonce(identity);
    }

    assertEquals(tryCount + 1, nonceProvider.incrementAndGetNonce(identity));
  }

  @Test
  public void testNonceGetOnMultiThread() throws Exception {
    final NonceProvider nonceProvider = new SimpleNonceProvider();
    final AccountAddress identity = new AergoKeyGenerator().create().getAddress();

    final int nThread = 3;
    final int tryCount = new Random().nextInt(100);
    final ExecutorService service = Executors.newFixedThreadPool(2);
    for (int i = 0; i < nThread; ++i) {
      service.submit(new Runnable() {
        @Override
        public void run() {
          for (int j = 0; j < tryCount; ++j) {
            nonceProvider.incrementAndGetNonce(identity);
          }
        }
      });
    }

    service.awaitTermination(5000L, TimeUnit.MILLISECONDS);
    assertEquals(tryCount * nThread, nonceProvider.getLastUsedNonce(identity));
  }

}
