/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertEquals;

import hera.AbstractTestCase;
import hera.api.model.Time;
import org.junit.Test;

public class SimpleTimeoutStrategyTest extends AbstractTestCase {

  @Test
  public void testGetTimeout() {
    final TimeoutStrategy timeoutStrategy = new SimpleTimeoutStrategy(3000L);
    assertEquals(Time.of(3000L, MILLISECONDS), timeoutStrategy.getTimeout());
  }

  @Test
  public void testGetTimeoutWithMinusValue() {
    final TimeoutStrategy timeoutStrategy = new SimpleTimeoutStrategy(-1L);
    assertEquals(Time.of(0L, MILLISECONDS), timeoutStrategy.getTimeout());
  }

}
