/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

public interface Function4<T0, T1, T2, T3, R> {

  /**
   * Applies this function to the given arguments.
   *
   * @param t0 the 1st argument
   * @param t1 the 2nd argument
   * @param t2 the 3rd argument
   * @param t3 the 4th argument
   * @return the function result
   */
  R apply(T0 t0, T1 t1, T2 t2, T3 t3);
}
