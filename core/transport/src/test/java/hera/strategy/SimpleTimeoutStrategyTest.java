/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertEquals;

import hera.AbstractTestCase;
import hera.api.model.Time;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.util.ThreadUtils;
import org.junit.Test;

public class SimpleTimeoutStrategyTest extends AbstractTestCase {

  @Test
  public void testGetTimeout() {
    final SimpleTimeoutStrategy timeoutStrategy = new SimpleTimeoutStrategy(3000L);
    assertEquals(Time.of(3000L, MILLISECONDS), timeoutStrategy.getTimeout());
  }

  @Test
  public void testGetTimeoutWithMinusValue() {
    final SimpleTimeoutStrategy timeoutStrategy = new SimpleTimeoutStrategy(-1L);
    assertEquals(Time.of(0L, MILLISECONDS), timeoutStrategy.getTimeout());
  }

  @Test(expected = Exception.class)
  public void shouldThrowException() {
    final SimpleTimeoutStrategy timeoutStrategy = new SimpleTimeoutStrategy(100L);
    timeoutStrategy.applyNext(() -> ResultOrErrorFutureFactory.supply(() -> {
      ThreadUtils.trySleep(10000L);
      return null;
    }), null).apply().get().getResult();
  }

  @Test
  public void shouldNotThrowException() {
    final SimpleTimeoutStrategy timeoutStrategy = new SimpleTimeoutStrategy(1000L);
    timeoutStrategy.applyNext(() -> ResultOrErrorFutureFactory.supply(() -> {
      ThreadUtils.trySleep(100L);
      return null;
    }), null).apply().get().getResult();
  }

}
