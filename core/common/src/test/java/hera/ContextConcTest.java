/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import hera.strategy.ConnectStrategy;
import org.junit.Test;

public class ContextConcTest extends AbstractTestCase {

  @Test
  public void testCopyOf() {
    final Context expected = new ContextConc();
    expected.addStrategy((ConnectStrategy<Object>) () -> null);
    final Context actual = ContextConc.copyOf(expected);
    assertEquals(expected, actual);
  }

  @Test
  public void testAddStrategyGetStrategy() {
    final Context context = new ContextConc();
    assertFalse(context.getStrategy(ConnectStrategy.class).isPresent());
    context.addStrategy((ConnectStrategy<Object>) () -> null);
    assertNotNull(context.getStrategy(ConnectStrategy.class).get());
  }

  @Test
  public void testStrategyOverride() {
    final Context context = new ContextConc();
    ConnectStrategy<Object> first = () -> null;
    ConnectStrategy<Object> second = () -> null;
    context.addStrategy(first);
    context.addStrategy(second);
    assertEquals(second, context.getStrategy(ConnectStrategy.class).get());
  }

}
