/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import static org.junit.Assert.fail;

import hera.AbstractTestCase;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.util.ThreadUtils;
import org.junit.Test;

public class SimpleTimeoutStrategyTest extends AbstractTestCase {

  @Test
  public void testTimeout() {
    final SimpleTimeoutStrategy timeoutStrategy = new SimpleTimeoutStrategy(1000L);
    timeoutStrategy.apply(() -> ResultOrErrorFutureFactory.supply(() -> {
      ThreadUtils.trySleep(100L);
      return null;
    })).get().getResult();
  }

  @Test
  public void shouldThrowException() {
    final SimpleTimeoutStrategy timeoutStrategy = new SimpleTimeoutStrategy(100L);
    try {
      timeoutStrategy.apply(() -> ResultOrErrorFutureFactory.supply(() -> {
        ThreadUtils.trySleep(10000L);
        return null;
      })).get().getResult();
      fail();
    } catch (Exception e) {
      // good we expected this
    }
  }

}
