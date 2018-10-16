/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import static org.junit.Assert.assertEquals;

import hera.AbstractTestCase;
import hera.api.model.Time;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

public class FutureTimeoutStrategyTest extends AbstractTestCase {

  @Test
  public void testGetTimeout() throws InterruptedException {
    final TimeoutStrategy timeoutStrategy = new FutureTimeoutStrategy(3000L);
    assertEquals(Time.of(3000L, TimeUnit.MILLISECONDS), timeoutStrategy.getTimeout());
  }

  @Test
  public void testGetTimeoutWithMinusValue() throws InterruptedException {
    final TimeoutStrategy timeoutStrategy = new FutureTimeoutStrategy(-1L);
    assertEquals(Time.of(0L, TimeUnit.MILLISECONDS), timeoutStrategy.getTimeout());
  }

}
