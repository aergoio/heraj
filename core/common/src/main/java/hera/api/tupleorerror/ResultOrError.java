/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

import static hera.api.tupleorerror.FunctionChain.fail;
import static hera.api.tupleorerror.FunctionChain.success;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class ResultOrError<T> extends AbstractTupleOrError {

  public ResultOrError(T result, Throwable error) {
    apply(new Object[] {result});
    setError(error);
  }

  public T getResult() {
    return !getValues().isEmpty() ? (T) unapply()[0] : null;
  }

  /**
   * Apply function to result if no error. Otherwise, don't apply and just keeping error.
   *
   * @param <R> type of applied result
   * @param fn function to apply
   * @return {@code ResultOrError} with values as result of fn
   */
  public <R> ResultOrError<R> thenApply(Function1<T, R> fn) {
    if (!hasError()) {
      try {
        R next = fn.apply(getResult());
        return success(next);
      } catch (Throwable error) {
        return fail(error);
      }
    }
    return fail(getError());
  }

  @Override
  public String toString() {
    return super.toString();
  }

  /**
   * Get result or throws exception if error exists.
   *
   * @param <X> Type of the exception to be thrown
   * @param exceptionSupplier exception supplier
   * @return result if no error
   * @throws X exception to throw when error exists
   */
  public <X extends Throwable> T getOrThrows(Supplier<? extends X> exceptionSupplier) throws X {
    if (null == getError()) {
      throw exceptionSupplier.get();
    }
    return getResult();
  }

}
