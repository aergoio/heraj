/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.fail;

import hera.AbstractTestCase;
import hera.api.model.internal.Time;
import hera.api.tupleorerror.Function0;
import hera.client.FinishableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

public class RetryStrategyTest extends AbstractTestCase {

  @Test
  public void testMinimumRetry() {
    try {
      final RetryStrategy retryStrategy =
          new RetryStrategy(-1, Time.of(100L, TimeUnit.MILLISECONDS));
      retryStrategy.action(null, new Function0<FinishableFuture<Integer>>() {
        @Override
        public FinishableFuture<Integer> apply() {
          final FinishableFuture<Integer> future = new FinishableFuture<Integer>();
          future.fail(new UnsupportedOperationException());
          return future;
        }
      }).get();
      fail();
    } catch (Exception e) {
      // good we expected this
    }
  }

  @Test
  public void testRetry() {
    final int count = 3;
    final RetryStrategy retryStrategy =
        new RetryStrategy(count, Time.of(100L, TimeUnit.MILLISECONDS));
    final CountDownLatch latch = new CountDownLatch(count);
    retryStrategy.action(null, new Function0<FinishableFuture<Integer>>() {
      @Override
      public FinishableFuture<Integer> apply() {
        final FinishableFuture<Integer> future = new FinishableFuture<Integer>();
        logger.debug("Current latch: {}", latch.getCount());
        if (latch.getCount() == 0) {
          future.success(randomUUID().toString().hashCode());
        } else {
          future.fail(new UnsupportedOperationException());
          latch.countDown();
        }
        return future;
      }
    }).get();
  }

  @Test(timeout = RetryStrategy.DEFAULT_RETRY_INTERVAL + 1000L)
  public void testDefaultRetryInterval() {
    final int count = 1;
    final RetryStrategy retryStrategy = new RetryStrategy(count);
    final CountDownLatch latch = new CountDownLatch(count);
    retryStrategy.action(null, new Function0<FinishableFuture<Integer>>() {
      @Override
      public FinishableFuture<Integer> apply() {
        final FinishableFuture<Integer> future = new FinishableFuture<Integer>();
        logger.debug("Current latch: {}", latch.getCount());
        if (latch.getCount() == 0) {
          future.success(randomUUID().toString().hashCode());
        } else {
          future.fail(new UnsupportedOperationException());
          latch.countDown();
        }
        return future;
      }
    }).get();
  }

}
