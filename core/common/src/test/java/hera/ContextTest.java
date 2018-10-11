/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import hera.strategy.ConnectStrategy;
import org.junit.Test;

public class ContextTest extends AbstractTestCase {

  @Test
  public void testAddStrategyGetStrategy() {
    final Context context = new Context();
    assertFalse(context.getStrategy(ConnectStrategy.class).isPresent());
    context.addStrategy((ConnectStrategy<Object>) () -> null);
    assertNotNull(context.getStrategy(ConnectStrategy.class).get());
  }

  @Test
  public void testStragetyOverride() {
    final Context context = new Context();
    ConnectStrategy<Object> first = (ConnectStrategy<Object>) () -> null;
    ConnectStrategy<Object> second = (ConnectStrategy<Object>) () -> null;
    context.addStrategy(first);
    context.addStrategy(second);
    assertEquals(second, context.getStrategy(ConnectStrategy.class).get());
  }

}
