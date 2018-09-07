/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

public interface Consumer2<T0, T1> {

  /**
   * Performs this operation on the given argument.
   *
   * @param t0 the 1st argument
   * @param t1 the 2nd argument
   */
  void accept(T0 t0, T1 t1);
}
