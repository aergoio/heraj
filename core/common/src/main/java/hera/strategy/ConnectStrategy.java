/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.ContextAware;
import hera.Strategy;

public interface ConnectStrategy<ConnectionT> extends Strategy, ContextAware {
  ConnectionT connect();
}
