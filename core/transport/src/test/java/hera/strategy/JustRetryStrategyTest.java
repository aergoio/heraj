/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.AbstractTestCase;
import hera.api.function.Function0;
import hera.api.model.internal.Time;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

public class JustRetryStrategyTest extends AbstractTestCase {

  @Test
  public void testRetry() {
    final AtomicInteger count = new AtomicInteger(3);
    final JustRetryStrategy retryStrategy =
        new JustRetryStrategy(count.get(), Time.of(100L, TimeUnit.MILLISECONDS));
    final Function0<Integer> strategyWrapped =
        retryStrategy.apply(new Function0<Integer>() {

          @Override
          public Integer apply() {
            if (0 < count.get()) {
              count.decrementAndGet();
              throw new IllegalStateException();
            }

            return 0;
          }
        });
    strategyWrapped.apply();
  }

  @Test(timeout = JustRetryStrategy.DEFAULT_RETRY_INTERVAL + 1000L)
  public void testDefaultRetryInterval() {
    final AtomicInteger count = new AtomicInteger(1);
    final JustRetryStrategy retryStrategy = new JustRetryStrategy(count.get());
    final Function0<Integer> wrapped =
        retryStrategy.apply(new Function0<Integer>() {

          @Override
          public Integer apply() {
            if (0 < count.get()) {
              count.decrementAndGet();
              throw new IllegalStateException();
            }

            return 0;
          }
        });
    wrapped.apply();
  }

}
