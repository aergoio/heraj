/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.function;

public interface Function3<T1, T2, T3, R> extends Function {

  /**
   * Applies this function to the given arguments.
   *
   * @param t1 the 1st argument
   * @param t2 the 2nd argument
   * @param t3 the 3rd argument
   * @return the function result
   */
  R apply(T1 t1, T2 t2, T3 t3);

}
