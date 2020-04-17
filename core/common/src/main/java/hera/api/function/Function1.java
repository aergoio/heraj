/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.function;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;

@ApiAudience.Private
@ApiStability.Unstable
public interface Function1<T, R> extends Function<R> {

  /**
   * Applies this function to the given argument.
   *
   * @param t the function argument
   * @return the function result
   */
  R apply(T t);

}
