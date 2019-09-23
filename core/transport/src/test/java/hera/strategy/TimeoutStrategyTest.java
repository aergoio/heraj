/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.fail;

import hera.AbstractTestCase;
import hera.api.function.Function0;
import hera.client.internal.FinishableFuture;
import hera.util.ThreadUtils;
import org.junit.Test;

public class TimeoutStrategyTest extends AbstractTestCase {

  @Test
  public void testTimeout() {
    final TimeoutStrategy timeoutStrategy = new TimeoutStrategy(1000L);
    final FinishableFuture<Integer> future = new FinishableFuture<Integer>();
    service.submit(new Runnable() {
      @Override
      public void run() {
        ThreadUtils.trySleep(100L);
        future.success(randomUUID().toString().hashCode());
      }
    });
    timeoutStrategy.apply(new Function0<FinishableFuture<Integer>>() {
      @Override
      public FinishableFuture<Integer> apply() {
        return future;
      }
    }).apply().get();
  }

  @Test
  public void shouldThrowException() {
    final TimeoutStrategy timeoutStrategy = new TimeoutStrategy(100L);
    try {
      final FinishableFuture<Integer> future = new FinishableFuture<Integer>();
      service.submit(new Runnable() {
        @Override
        public void run() {
          ThreadUtils.trySleep(10000L);
          future.success(randomUUID().toString().hashCode());
        }
      });
      timeoutStrategy.apply(new Function0<FinishableFuture<Integer>>() {
        @Override
        public FinishableFuture<Integer> apply() {
          return future;
        }
      }).apply().get();
      fail();
    } catch (Exception e) {
      // good we expected this
    }
  }

}
