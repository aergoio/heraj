/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;

public class AbstractKeyStoreTest extends AbstractTestCase {

  @Test
  public void testUnlockConcurrently() throws Exception {
    // given
    final AbstractKeyStore keyStore = spy(AbstractKeyStore.class);
    final AergoKey key = new AergoKeyGenerator().create();
    final Authentication authentication =
        Authentication.of(key.getAddress(), randomUUID().toString());
    when(keyStore.loadAergoKey(authentication)).thenReturn(key);

    // when
    final AtomicBoolean first = new AtomicBoolean(true);
    final int nThreads = 2 * Runtime.getRuntime().availableProcessors();
    final ExecutorService service = Executors.newFixedThreadPool(nThreads);
    final Runnable runnable = new Runnable() {

      @Override
      public void run() {
        logger.info("Request for {} started", Thread.currentThread());
        AccountAddress unlocked = keyStore.unlock(authentication);
        if (first.getAndSet(false)) {
          assertNotNull(unlocked);
        } else {
          assertNull(unlocked);
        }
        logger.info("Request for {} terminated", Thread.currentThread());
      }
    };

    // then
    List<Future<?>> futures = new ArrayList<>();
    for (int i = 0; i < nThreads; ++i) {
      futures.add(service.submit(runnable));
    }
    for (final Future<?> future : futures) {
      future.get();
    }
  }

  @Test
  public void testLockConcurrently() throws Exception {
    // given
    final AbstractKeyStore keyStore = spy(AbstractKeyStore.class);
    final AergoKey key = new AergoKeyGenerator().create();
    final Authentication authentication =
        Authentication.of(key.getAddress(), randomUUID().toString());
    when(keyStore.loadAergoKey(authentication)).thenReturn(key);
    keyStore.unlock(authentication);

    // when
    final AtomicBoolean first = new AtomicBoolean(true);
    final int nThreads = 2 * Runtime.getRuntime().availableProcessors();
    final ExecutorService service = Executors.newFixedThreadPool(nThreads);
    final Runnable runnable = new Runnable() {

      @Override
      public void run() {
        logger.info("Request for {} started", Thread.currentThread());
        boolean unlockResult = keyStore.lock(authentication);
        if (first.getAndSet(false)) {
          assertTrue(unlockResult);
        } else {
          assertFalse(unlockResult);
        }
        logger.info("Request for {} terminated", Thread.currentThread());
      }
    };

    // then
    List<Future<?>> futures = new ArrayList<>();
    for (int i = 0; i < nThreads; ++i) {
      futures.add(service.submit(runnable));
    }
    for (final Future<?> future : futures) {
      future.get();
    }
  }

}
