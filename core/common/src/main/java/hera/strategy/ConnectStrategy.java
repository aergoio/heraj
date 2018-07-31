/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.Strategy;

public interface ConnectStrategy<ConnectionT> extends Strategy {
  ConnectionT connect();
}
