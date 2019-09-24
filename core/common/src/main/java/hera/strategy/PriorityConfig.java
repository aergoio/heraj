/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import static hera.util.ValidationUtils.assertNotNull;
import static hera.util.ValidationUtils.assertTrue;
import static java.util.Collections.unmodifiableMap;

import hera.Strategy;
import java.util.HashMap;
import java.util.Map;

public class PriorityConfig {

  protected final Map<Class<? extends Strategy>, Integer> strategy2Priority = new HashMap<>();

  /**
   * Set priority of strategy.
   *
   * @param strategy a strategy to set priority
   * @param priority a priority. must be positive
   * @return an instance of this
   */
  public PriorityConfig set(final Class<? extends Strategy> strategy, final int priority) {
    assertNotNull(strategy);
    assertTrue(0 < priority, "Priority must be positive");
    this.strategy2Priority.put(strategy, priority);
    return this;
  }

  public Map<Class<? extends Strategy>, Integer> getStrategy2Priority() {
    return unmodifiableMap(this.strategy2Priority);
  }

}
