/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.function;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;

@ApiAudience.Private
@ApiStability.Unstable
public interface Function2<T1, T2, R> extends Function<R> {

  /**
   * Applies this function to the given arguments.
   *
   * @param t1 the 1st argument
   * @param t2 the 2nd argument
   * @return the function result
   */
  R apply(T1 t1, T2 t2);

}
