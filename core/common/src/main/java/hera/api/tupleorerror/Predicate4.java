/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

@FunctionalInterface
public interface Predicate4<T1, T2, T3, T4> {

  /**
   * Evaluates this predicate on the given arguments.
   *
   * @param t1 the 1st input argument
   * @param t2 the 2nd input argument
   * @param t3 the 3rd input argument
   * @param t4 the 4th input argument
   * @return {@code true} if the input argument matches the predicate, otherwise {@code false}
   */
  boolean test(T1 t1, T2 t2, T3 t3, T4 t4);
}
