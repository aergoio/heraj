/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.Strategy;
import hera.api.model.Time;

public interface TimeoutStrategy extends Strategy {
  Time getTimeout();
}
