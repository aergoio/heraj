/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.Strategy;

public interface TimeoutStrategy extends Strategy {

  /**
   * Get timeout value in milliseconds.
   *
   * @return timeout in milliseconds
   */
  long getTimeout();

}
