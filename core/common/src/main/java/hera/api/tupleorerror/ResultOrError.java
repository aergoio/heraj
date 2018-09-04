package hera.api.tupleorerror;

import hera.exception.HerajException;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ResultOrError<T> extends Tuple1<T>, WithError {

  /**
   * If a result is present, returns the value, otherwise throws {@code HerajException}.
   *
   * @return the non-null result
   * @throws HerajException if there is no result present
   *
   * @see ResultOrError#hasResult()
   */
  T getResult();

  /**
   * If a result is present, return true, otherwise return false.
   *
   * @return whether a result is present or not
   */
  default boolean hasResult() {
    return false == hasError();
  }

  /**
   * If a result is present, invoke the specified consumer with the value, otherwise do nothing.
   *
   * @param consumer block to be executed if a result is present
   */
  void ifPresent(Consumer<? super T> consumer);

  /**
   * Apply function to result if no error. Otherwise, don't apply and just keeping error.
   *
   * @param <R> type of applied result
   * @param fn function to apply
   * @return {@code ResultOrError} with values as result of fn
   */
  <R> ResultOrError<R> map(Function1<T, R> fn);

  /**
   * Apply function to result if no error. Otherwise, don't apply and just keeping error. This
   * method is similar to {@link #map(Function1)}, but the provided mapper is one whose result is
   * already an {@code ResultOrError},
   *
   * @param <R> type of applied result
   * @param fn function to apply
   * @return {@code ResultOrError} with values as result of fn
   */
  <R> ResultOrError<R> flatMap(Function1<T, ResultOrError<R>> fn);

  /**
   * Return the value if present, otherwise return {@code other}.
   *
   * @param other the value to be returned if there is no value present, may be null
   * @return the value, if present, otherwise {@code other}
   */
  T orElse(T other);

  /**
   * Return the value if present, otherwise invoke {@code other} and return the result of that
   * invocation.
   *
   * @param other a {@code Supplier} whose result is returned if no value is present
   * @return the value if present otherwise the result of {@code other.get()}
   */
  T orElseGet(Supplier<? extends T> other);

  /**
   * Get result or throws exception if error exists.
   *
   * @param <X> Type of the exception to be thrown
   * @param exceptionSupplier exception supplier
   * @return result if no error
   * @throws X exception to throw when error exists
   */
  <X extends Throwable> T getOrThrows(Supplier<? extends X> exceptionSupplier) throws X;
}
