/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

public interface ResultOrErrorFuture<T>
    extends FinishableFuture<ResultOrError<T>>, ErrorHoldableFuture<ResultOrError<T>> {

  /**
   * If a future operation is complete and a result is present, invoke the specified consumer with
   * the value and hold result as true. If invoke fails with exception. then hold corresponding
   * error in {@code ResultOrError}.
   *
   * @param consumer block to be executed if a result is present after getting results
   * @return a {@code ResultOrErrorFuture} describing consumer result
   */
  ResultOrErrorFuture<Boolean> ifPresent(Consumer1<? super T> consumer);

  /**
   * If a future operation is complete and a result is present, and the value matches the given
   * predicate, return a {@code ResultOrErrorFuture} describing the value, otherwise return a
   * {@code ResultOrErrorFuture} with {@code ResultOrError} holding {@code HerajException}.
   *
   * @param predicate a predicate to apply to the value, if present
   * @return a {@code ResultOrErrorFuture} describing the value of this {@code ResultOrErrorFuture}
   *         if a value is present and the value matches the given predicate, otherwise a
   *         {@code ResultOrErrorFuture} with {@code ResultOrError} holding {@code HerajException}
   */
  ResultOrErrorFuture<T> filter(Predicate1<? super T> predicate);

  /**
   * Apply function to result when it's complete without error. Otherwise, don't apply and just
   * keeping error.
   *
   * @param <R> type of next ResultOrErrorFuture
   * @param fn function to apply
   * @return {@code ResultOrErrorFuture} with values as result of fn
   */
  <R> ResultOrErrorFuture<R> map(Function1<? super T, ? extends R> fn);

  /**
   * Apply function to result when it's complete without error. Otherwise, don't apply and just
   * keeping error. This method is similar to {@link #map(Function1)}, but the provided mapper is
   * one whose result is already an {@code ResultOrErrorFuture},
   *
   * @param <R> type of next ResultOrErrorFuture
   * @param fn function to apply
   * @return {@code ResultOrErrorFuture} with values as result of fn
   */
  <R> ResultOrErrorFuture<R> flatMap(Function1<? super T, ResultOrErrorFuture<R>> fn);

}
