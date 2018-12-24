/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import static org.junit.Assert.fail;

import hera.AbstractTestCase;
import hera.api.model.internal.Time;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

public class RetryStrategyTest extends AbstractTestCase {

  @Test
  public void testMinimumRetry() {
    final RetryStrategy retryStrategy =
        new RetryStrategy(-1, Time.of(100L, TimeUnit.MILLISECONDS));
    final CountDownLatch latch = new CountDownLatch(RetryStrategy.DEFAULT_RETRY_COUNT + 1);
    try {
      retryStrategy.action(null, () -> ResultOrErrorFutureFactory.supply(() -> {
        if (latch.getCount() == 0) {
          return null;
        }
        latch.countDown();
        throw new UnsupportedOperationException();
      })).get().getResult();
      fail();
    } catch (Exception e) {
      // good we expected this
    }
  }

  @Test
  public void testRetry() {
    final int retryCount = 3;
    final RetryStrategy retryStrategy =
        new RetryStrategy(retryCount, Time.of(100L, TimeUnit.MILLISECONDS));
    final CountDownLatch latch = new CountDownLatch(retryCount);
    retryStrategy.action(null, () -> ResultOrErrorFutureFactory.supply(() -> {
      if (latch.getCount() == 0) {
        return null;
      }
      latch.countDown();
      throw new UnsupportedOperationException();
    })).get().getResult();
  }

  @Test(timeout = RetryStrategy.DEFAULT_RETRY_INTERVAL + 1000L)
  public void testDefaultRetryInterval() {
    final int retryCount = 1;
    final RetryStrategy retryStrategy = new RetryStrategy(retryCount);
    final CountDownLatch latch = new CountDownLatch(retryCount);
    retryStrategy.action(null, () -> ResultOrErrorFutureFactory.supply(() -> {
      if (latch.getCount() == 0) {
        return null;
      }
      latch.countDown();
      throw new UnsupportedOperationException();
    })).get().getResult();
  }

}
