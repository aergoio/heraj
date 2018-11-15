/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

@FunctionalInterface
public interface Consumer2<T1, T2> {

  /**
   * Performs this operation on the given argument.
   *
   * @param t1 the 1st argument
   * @param t2 the 2nd argument
   */
  void accept(T1 t1, T2 t2);
}
