/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.Strategy;
import hera.api.SignOperation;

public interface SignStrategy<ConnectionT> extends Strategy {
  SignOperation getSignOperation();
}
