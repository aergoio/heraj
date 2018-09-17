/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

public interface Tuple4OrErrorFuture<T1, T2, T3, T4>
    extends GetNotThrowingFuture<Tuple4OrError<T1, T2, T3, T4>> {

  /**
   * Complete Tuple4OrErrorFuture with Tuple4OrError.
   *
   * @param tuple4OrError Tuple4OrError
   * @return complete result
   */
  boolean complete(Tuple4OrError<T1, T2, T3, T4> tuple4OrError);

  /**
   * If a future operation is complete and a values are present, invoke the specified consumer with
   * the values and hold result as true. If invoke fails with exception. then hold corresponding
   * error in {@code ResultOrError}.
   *
   * @param consumer block to be executed if a values are present
   * @return a {@code ResultOrErrorFuture} describing consumer result
   */
  ResultOrErrorFuture<Boolean> ifPresent(
      Consumer4<? super T1, ? super T2, ? super T3, ? super T4> consumer);

  /**
   * If a future operation is complete and a values are present, and the values match the given
   * predicate, return a {@code Tuple4OrErrorFuture} describing the values, otherwise return a
   * {@code Tuple4OrErrorFuture} with {@code Tuple4OrError} holding {@code HerajException}.
   *
   * @param predicate a predicate to apply to the values, if present
   * @return a {@code Tuple4OrErrorFuture} describing the value of this {@code Tuple4OrErrorFuture}
   *         if a value is present and the values match the given predicate, otherwise a
   *         {@code Tuple4OrErrorFuture} with {@code Tuple4OrError} holding {@code HerajException}
   */
  Tuple4OrErrorFuture<T1, T2, T3, T4> filter(
      Predicate4<? super T1, ? super T2, ? super T3, ? super T4> predicate);

  /**
   * Apply function to values when it's complete without error. Otherwise, don't apply and just
   * keeping error.
   *
   * @param <R> type of next ResultOrErrorFuture
   * @param fn function to apply
   * @return {@code ResultOrErrorFuture} with values as result of fn
   */
  <R> ResultOrErrorFuture<R> map(
      Function4<? super T1, ? super T2, ? super T3, ? super T4, ? extends R> fn);

}
