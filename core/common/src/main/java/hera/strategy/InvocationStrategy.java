/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.Invocation;
import hera.Strategy;

public interface InvocationStrategy extends Strategy {

  <T> Invocation<T> apply(Invocation<T> invocation);

}
