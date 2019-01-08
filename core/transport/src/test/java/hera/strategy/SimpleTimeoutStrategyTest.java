/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.fail;

import hera.AbstractTestCase;
import hera.api.function.Function0;
import hera.client.FinishableFuture;
import hera.util.ThreadUtils;
import org.junit.Test;

public class SimpleTimeoutStrategyTest extends AbstractTestCase {

  @Test
  public void testTimeout() {
    final SimpleTimeoutStrategy timeoutStrategy = new SimpleTimeoutStrategy(1000L);
    final FinishableFuture<Integer> future = new FinishableFuture<Integer>();
    service.submit(new Runnable() {
      @Override
      public void run() {
        ThreadUtils.trySleep(100L);
        future.success(randomUUID().toString().hashCode());
      }
    });
    timeoutStrategy.action(null, new Function0<FinishableFuture<Integer>>() {
      @Override
      public FinishableFuture<Integer> apply() {
        return future;
      }
    });
  }

  @Test
  public void shouldThrowException() {
    final SimpleTimeoutStrategy timeoutStrategy = new SimpleTimeoutStrategy(100L);
    try {
      final FinishableFuture<Integer> future = new FinishableFuture<Integer>();
      service.submit(new Runnable() {
        @Override
        public void run() {
          ThreadUtils.trySleep(10000L);
          future.success(randomUUID().toString().hashCode());
        }
      });
      timeoutStrategy.action(null, new Function0<FinishableFuture<Integer>>() {
        @Override
        public FinishableFuture<Integer> apply() {
          return future;
        }
      }).get();
      fail();
    } catch (Exception e) {
      // good we expected this
    }
  }

}
