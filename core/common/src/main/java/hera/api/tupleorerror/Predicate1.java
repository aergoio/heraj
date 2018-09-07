/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

public interface Predicate1<T> {

  /**
   * Evaluates this predicate on the given argument.
   *
   * @param t the input argument
   * @return {@code true} if the input argument matches the predicate, otherwise {@code false}
   */
  boolean test(T t);
}
