/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

import java.util.Objects;

@FunctionalInterface
public interface Function0<R> extends Function {

  /**
   * Applies this function. This function is like supplier.
   *
   * @return the function result
   */
  R apply();

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
  default <V> Function0<V> andThen(Function1<? super R, ? extends V> after) {
    Objects.requireNonNull(after);
    return () -> after.apply(apply());
  }

}
