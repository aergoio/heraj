package hera.api.tupleorerror;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.exception.HerajException;
import java.util.function.Supplier;

@ApiAudience.Public
@ApiStability.Unstable
public interface ResultOrError<T> extends WithError {

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
    return !hasError();
  }

  /**
   * If a result is present, invoke the specified consumer with the value, otherwise do nothing.
   *
   * @param consumer block to be executed if a result is present
   */
  void ifPresent(Consumer1<? super T> consumer);

  /**
   * If a result is present, and the value matches the given predicate, return a
   * {@code ResultOrError} describing the value, otherwise return a {@code ResultOrError} with
   * {@code HerajException}.
   *
   * @param predicate a predicate to apply to the value, if present
   * @return a {@code ResultOrError} describing the value of this {@code ResultOrError} if a value
   *         is present and the value matches the given predicate, otherwise a {@code ResultOrError}
   *         with {@code HerajException}
   */
  ResultOrError<T> filter(Predicate1<? super T> predicate);

  /**
   * Apply function to result if no error. Otherwise, don't apply and just keeping error.
   *
   * @param <R> type of applied result
   * @param fn function to apply
   * @return {@code ResultOrError} with values as result of fn
   */
  <R> ResultOrError<R> map(Function1<? super T, ? extends R> fn);

  /**
   * Apply function to result if no error. Otherwise, don't apply and just keeping error. This
   * method is similar to {@link #map(Function1)}, but the provided mapper is one whose result is
   * already a {@code ResultOrError},
   *
   * @param <R> type of applied result
   * @param fn function to apply
   * @return {@code ResultOrError} with values as result of fn
   */
  <R> ResultOrError<R> flatMap(Function1<? super T, ResultOrError<R>> fn);

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
