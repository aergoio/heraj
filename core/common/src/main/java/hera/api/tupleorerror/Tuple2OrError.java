package hera.api.tupleorerror;

import hera.exception.HerajException;

public interface Tuple2OrError<T1, T2> extends WithError {

  /**
   * If a 1st element of tuple is present, returns the value, otherwise throws
   * {@code HerajException}.
   *
   * @return the non-null result
   * @throws HerajException if there is no result present
   */
  T1 get1();

  /**
   * If a 2nd element of tuple is present, returns the value, otherwise throws
   * {@code HerajException}.
   *
   * @return the non-null result
   * @throws HerajException if there is no result present
   */
  T2 get2();

  /**
   * If a result is present, invoke the specified consumer with the value, otherwise do nothing.
   *
   * @param consumer block to be executed if a result is present
   */
  void ifPresent(Consumer2<? super T1, ? super T2> consumer);

  /**
   * If a result is present, and the values matches the given predicate, return a
   * {@code Tuple2OrError} describing the value, otherwise return a {@code Tuple2OrError} with
   * {@code HerajException}.
   *
   * @param predicate a predicate to apply to the value, if present
   * @return a {@code Tuple2OrError} describing the value of this {@code Tuple2OrError} if a value
   *         is present and the value matches the given predicate, otherwise a {@code Tuple2OrError}
   *         with {@code HerajException}
   */
  Tuple2OrError<T1, T2> filter(Predicate2<? super T1, ? super T2> predicate);

  /**
   * Apply function to values if no error. Otherwise, don't apply and just keeping error.
   *
   * @param <R> type of applied result
   * @param fn function to apply
   * @return {@code ResultOrError} with values as result of fn
   */
  <R> ResultOrError<R> map(Function2<? super T1, ? super T2, ? extends R> fn);

}
