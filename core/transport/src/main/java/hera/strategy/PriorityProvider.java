/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

public class PriorityProvider {

  protected static final PriorityConfig priorityConfig;

  static {
    priorityConfig = new PriorityConfig();
    priorityConfig.set(TimeoutStrategy.class, 1);
    priorityConfig.set(JustRetryStrategy.class, 2);
  }

  public static PriorityConfig get() {
    return priorityConfig;
  }

}
