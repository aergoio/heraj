/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.function;

public interface Function1<T, R> extends Function<R> {

  /**
   * Applies this function to the given argument.
   *
   * @param t the function argument
   * @return the function result
   */
  R apply(T t);

}
