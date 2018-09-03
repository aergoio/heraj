package hera.api.tupleorerror;

import java.util.function.Supplier;

public interface ResultOrError<T> extends Tuple1<T>, WithError {

  default boolean hasResult() {
    return null != getResult();
  }

  /**
   * Apply function to result if no error. Otherwise, don't apply and just keeping error.
   *
   * @param <R> type of applied result
   * @param fn function to apply
   * @return {@code ResultOrError} with values as result of fn
   */
  <R> ResultOrError<R> thenApply(Function1<T, R> fn);

  /**
   * Apply function to result if no error. Otherwise, don't apply and just keeping error. This
   * method is similar to {@link #map(Function1uncion1}, but the provided mapper is one whose result
   * is already an {@code ResultOrError},
   *
   * @param <R> type of applied result
   * @param fn function to apply
   * @return {@code ResultOrError} with values as result of fn
   */
  <R> ResultOrError<R> flatMap(Function1<T, ResultOrError<R>> fn);

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
