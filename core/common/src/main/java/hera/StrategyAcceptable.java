/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import hera.strategy.StrategyChain;

public interface StrategyAcceptable {
  void accept(StrategyChain strategyChain);
}
