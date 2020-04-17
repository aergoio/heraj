/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.Invocation;
import hera.Strategy;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;

@ApiAudience.Private
@ApiStability.Unstable
public interface InvocationStrategy extends Strategy {

  <T> Invocation<T> apply(Invocation<T> invocation);

}
