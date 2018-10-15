/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

public interface Tuple3OrErrorFuture<T1, T2, T3>
    extends EitherFuture<Tuple3OrError<T1, T2, T3>> {

  /**
   * If a future operation is complete and a values are present, invoke the specified consumer with
   * the values and hold result as true. If invoke fails with exception. then hold corresponding
   * error in {@code ResultOrError}.
   *
   * @param consumer block to be executed if a values are present
   * @return a {@code ResultOrErrorFuture} describing consumer result
   */
  ResultOrErrorFuture<Boolean> ifPresent(Consumer3<? super T1, ? super T2, ? super T3> consumer);

  /**
   * If a future operation is complete and a values are present, and the values match the given
   * predicate, return a {@code Tuple3OrErrorFuture} describing the value, otherwise return a
   * {@code Tuple3OrErrorFuture} with {@code Tuple3OrError} holding {@code HerajException}.
   *
   * @param predicate a predicate to apply to the values, if present
   * @return a {@code Tuple3OrErrorFuture} describing the value of this {@code Tuple3OrErrorFuture}
   *         if a value is present and the values match the given predicate, otherwise a
   *         {@code Tuple3OrErrorFuture} with {@code Tuple3OrError} holding {@code HerajException}
   */
  Tuple3OrErrorFuture<T1, T2, T3> filter(Predicate3<? super T1, ? super T2, ? super T3> predicate);

  /**
   * Apply function to values when it's complete without error. Otherwise, don't apply and just
   * keeping error.
   *
   * @param <R> type of next ResultOrErrorFuture
   * @param fn function to apply
   * @return {@code ResultOrErrorFuture} with values as result of fn
   */
  <R> ResultOrErrorFuture<R> map(Function3<? super T1, ? super T2, ? super T3, ? extends R> fn);

}
