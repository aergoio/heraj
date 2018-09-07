package hera.api.tupleorerror;

import hera.exception.HerajException;

public interface Tuple4OrError<T1, T2, T3, T4> extends WithError {

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
   * If a 4th element of tuple is present, returns the value, otherwise throws
   * {@code HerajException}.
   *
   * @return the non-null result
   * @throws HerajException if there is no result present
   */
  T4 get4();

  /**
   * If a result is present, invoke the specified consumer with the value, otherwise do nothing.
   *
   * @param consumer block to be executed if a result is present
   */
  void ifPresent(Consumer4<? super T1, ? super T2, ? super T3, ? super T4> consumer);

  /**
   * If a result is present, and the values matches the given predicate, return a
   * {@code Tuple4OrError} describing the value, otherwise return a {@code Tuple4OrError} with
   * {@code HerajException}.
   *
   * @param predicate a predicate to apply to the value, if present
   * @return a {@code Tuple4OrError} describing the value of this {@code Tuple4OrError} if a value
   *         is present and the value matches the given predicate, otherwise a {@code Tuple4OrError}
   *         with {@code HerajException}
   */
  Tuple4OrError<T1, T2, T3, T4> filter(
      Predicate4<? super T1, ? super T2, ? super T3, ? super T4> predicate);

  /**
   * Apply function to values if no error. Otherwise, don't apply and just keeping error.
   *
   * @param <R> type of applied result
   * @param fn function to apply
   * @return {@code ResultOrError} with values as result of fn
   */
  <R> ResultOrError<R> map(
      Function4<? super T1, ? super T2, ? super T3, ? super T4, ? extends R> fn);

}
