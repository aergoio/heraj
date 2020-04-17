/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import static org.junit.Assert.fail;

import hera.AbstractTestCase;
import hera.Invocation;
import hera.RequestMethod;
import hera.api.model.Time;
import hera.util.ThreadUtils;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.Test;

public class TimeoutStrategyTest extends AbstractTestCase {

  @Test
  public void testTimeout() throws Exception {
    final TimeoutStrategy strategy = new TimeoutStrategy(Time.of(1000L, TimeUnit.MILLISECONDS));
    final RequestMethod<Integer> method = new RequestMethod<Integer>() {

      @Override
      public String getName() {
        return null;
      }

      @Override
      protected Integer runInternal(List<Object> parameters) throws Exception {
        return 0;
      }
    };
    final Invocation<Integer> invocation = strategy.apply(method.toInvocation());
    invocation.invoke();
  }

  @Test
  public void shouldThrowException() {
    try {
      final TimeoutStrategy strategy = new TimeoutStrategy(Time.of(100L, TimeUnit.MILLISECONDS));
      final RequestMethod<Integer> method = new RequestMethod<Integer>() {

        @Override
        public String getName() {
          return null;
        }

        @Override
        protected Integer runInternal(List<Object> parameters) throws Exception {
          ThreadUtils.trySleep(1000L);
          return 0;
        }
      };
      final Invocation<Integer> invocation = strategy.apply(method.toInvocation());
      invocation.invoke();
      fail();
    } catch (TimeoutException e) {
      // good we expected this
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

}
