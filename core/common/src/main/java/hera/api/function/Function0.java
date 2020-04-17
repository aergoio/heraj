/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.function;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;

@ApiAudience.Private
@ApiStability.Unstable
public interface Function0<R> extends Function<R> {

  /**
   * Applies this function. This function is like supplier.
   *
   * @return the function result
   */
  R apply();

}
