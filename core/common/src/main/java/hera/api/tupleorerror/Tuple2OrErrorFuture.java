/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

public interface Tuple2OrErrorFuture<T1, T2>
    extends FinishableFuture<Tuple2OrError<T1, T2>>, ErrorHoldableFuture<Tuple2OrError<T1, T2>> {

  /**
   * If a future operation is complete and a values are present, invoke the specified consumer with
   * the values and hold result as true. If invoke fails with exception. then hold corresponding
   * error in {@code ResultOrError}.
   *
   * @param consumer block to be executed if a values are present
   * @return a {@code ResultOrErrorFuture} describing consumer result
   */
  ResultOrErrorFuture<Boolean> ifPresent(Consumer2<? super T1, ? super T2> consumer);

  /**
   * If a future operation is complete and a values are present, and the values match the given
   * predicate, return a {@code ResultOrErrorFuture} describing the value, otherwise return a
   * {@code ResultOrErrorFuture} with {@code ResultOrError} holding {@code HerajException}.
   *
   * @param predicate a predicate to apply to the values, if present
   * @return a {@code Tuple2OrErrorFuture} describing the value of this {@code Tuple2OrErrorFuture}
   *         if a value is present and the values match the given predicate, otherwise a
   *         {@code Tuple2OrErrorFuture} with {@code Tuple2OrError} holding {@code HerajException}
   */
  Tuple2OrErrorFuture<T1, T2> filter(Predicate2<? super T1, ? super T2> predicate);

  /**
   * Apply function to values when it's complete without error. Otherwise, don't apply and just
   * keeping error.
   *
   * @param <R> type of next ResultOrErrorFuture
   * @param fn function to apply
   * @return {@code ResultOrErrorFuture} with values as result of fn
   */
  <R> ResultOrErrorFuture<R> map(Function2<? super T1, ? super T2, ? extends R> fn);

}
