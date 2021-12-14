/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.transaction;

import static org.junit.Assert.assertEquals;

import hera.AbstractTestCase;
import hera.TestUtils;
import hera.api.model.AccountAddress;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

public class SimpleNonceProviderTest extends AbstractTestCase {
  @BeforeClass
  public static void beforeClass() throws Exception {
    // powermock cannot mock java.security packages in jdk17 due to stricter security policies
    Assume.assumeTrue(TestUtils.getVersion() < 17 );
  }

  @Test
  public void testLimitCapacity() {
    final int capacity = 3;
    final SimpleNonceProvider nonceProvider = new SimpleNonceProvider(capacity);

    for (int i = 0; i < 5 * capacity; ++i) {
      final AergoKey key = new AergoKeyGenerator().create();
      nonceProvider.bindNonce(key.getAddress(), 3L);
    }

    assertEquals(capacity, nonceProvider.cache.size());
  }

  @Test
  public void shoultLeastRecentlyUsedAddressRemoved() {
    final int capacity = 3;
    final SimpleNonceProvider nonceProvider = new SimpleNonceProvider(capacity);

    final AccountAddress stale = new AergoKeyGenerator().create().getAddress();
    for (int i = 0; i < capacity; ++i) {
      final AccountAddress address = new AergoKeyGenerator().create().getAddress();
      nonceProvider.bindNonce(address, 3L);
    }

    assertEquals(capacity, nonceProvider.cache.size());
    assertEquals(0L, nonceProvider.getLastUsedNonce(stale));
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
    // powermock cannot mock java.security packages in jdk17 due to stricter security policies
    Assume.assumeTrue(TestUtils.getVersion() < 17 );

    // given
    final NonceProvider nonceProvider = new SimpleNonceProvider();
    final AccountAddress identity = new AergoKeyGenerator().create().getAddress();

    // when
    final int nThread = 2 * Runtime.getRuntime().availableProcessors();
    final int tryCount = new Random().nextInt(1000);
    final ExecutorService service = Executors.newFixedThreadPool(nThread);
    final List<Future<?>> futures = new ArrayList<>();
    for (int i = 0; i < nThread; ++i) {
      final Future<?> future = service.submit(new Runnable() {
        @Override
        public void run() {
          for (int j = 0; j < tryCount; ++j) {
            nonceProvider.incrementAndGetNonce(identity);
          }
        }
      });
      futures.add(future);
    }

    // then
    for (final Future<?> future : futures) {
      future.get();
    }
    assertEquals(tryCount * nThread, nonceProvider.getLastUsedNonce(identity));
  }

  @Test
  public void testReleaseNonce() {

  }
}
