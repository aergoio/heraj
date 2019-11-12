/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import static org.junit.Assert.fail;

import hera.AbstractTestCase;
import hera.api.function.Function0;
import hera.util.ThreadUtils;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.junit.Test;

public class TimeoutStrategyTest extends AbstractTestCase {

  @Test
  public void testTimeout() throws InterruptedException, ExecutionException {
    final Future<Integer> future = service.submit(new Callable<Integer>() {
      @Override
      public Integer call() {
        ThreadUtils.trySleep(100L);
        return 0;
      }
    });
    new TimeoutStrategy(1000L).apply(new Function0<Future<Integer>>() {

      @Override
      public Future<Integer> apply() {
        return future;
      }
    }).apply().get();
  }

  @Test
  public void shouldThrowException() throws InterruptedException, ExecutionException {
    final Future<Integer> future = service.submit(new Callable<Integer>() {
      @Override
      public Integer call() {
        ThreadUtils.trySleep(1000L);
        return 0;
      }
    });
    try {
      new TimeoutStrategy(100L).apply(new Function0<Future<Integer>>() {

        @Override
        public Future<Integer> apply() {
          return future;
        }
      }).apply().get();
      fail();
    } catch (Exception e) {
      // good we expected this
    }
  }

}
