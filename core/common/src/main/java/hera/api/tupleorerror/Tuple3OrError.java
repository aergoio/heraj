package hera.api.tupleorerror;

import hera.exception.HerajException;

public interface Tuple3OrError<T1, T2, T3> extends WithError {

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
   * If a 3rd element of tuple is present, returns the value, otherwise throws
   * {@code HerajException}.
   *
   * @return the non-null result
   * @throws HerajException if there is no result present
   */
  T3 get3();

  /**
   * If a result is present, invoke the specified consumer with the value, otherwise do nothing.
   *
   * @param consumer block to be executed if a result is present
   */
  void ifPresent(Consumer3<? super T1, ? super T2, ? super T3> consumer);

  /**
   * If a result is present, and the values matches the given predicate, return a
   * {@code Tuple3OrError} describing the value, otherwise return a {@code Tuple3OrError} with
   * {@code HerajException}.
   *
   * @param predicate a predicate to apply to the value, if present
   * @return a {@code Tuple3OrError} describing the value of this {@code Tuple3OrError} if a value
   *         is present and the value matches the given predicate, otherwise a {@code Tuple3OrError}
   *         with {@code HerajException}
   */
  Tuple3OrError<T1, T2, T3> filter(Predicate3<? super T1, ? super T2, ? super T3> predicate);

  /**
   * Apply function to values if no error. Otherwise, don't apply and just keeping error.
   *
   * @param <R> type of applied result
   * @param fn function to apply
   * @return {@code ResultOrError} with values as result of fn
   */
  <R> ResultOrError<R> map(Function3<? super T1, ? super T2, ? super T3, ? extends R> fn);

}
