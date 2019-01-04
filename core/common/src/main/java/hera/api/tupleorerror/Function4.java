/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

import java.util.Objects;

public interface Function4<T1, T2, T3, T4, R> extends Function {

  /**
   * Applies this function to the given arguments.
   *
   * @param t1 the 1st argument
   * @param t2 the 2nd argument
   * @param t3 the 3rd argument
   * @param t4 the 4th argument
   * @return the function result
   */
  R apply(T1 t1, T2 t2, T3 t3, T4 t4);

  /**
   * Returns a composed function that first applies this function to its input, and then applies the
   * {@code after} function to the result.
   *
   * @param <V> the type of output of the {@code after} function, and of the composed function
   * @param after the function to apply after this function is applied
   * @return a composed function that first applies this function and then applies the {@code after}
   *         function
   * @throws NullPointerException if after is null
   */
  default <V> Function4<T1, T2, T3, T4, V> andThen(Function1<? super R, ? extends V> after) {
    Objects.requireNonNull(after);
    return (T1 t1, T2 t2, T3 t3, T4 t4) -> after.apply(apply(t1, t2, t3, t4));
  }

}
