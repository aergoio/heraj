/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.function;

public interface Function0<R> extends Function {

  /**
   * Applies this function. This function is like supplier.
   *
   * @return the function result
   */
  R apply();

}
