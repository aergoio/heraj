/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

import static hera.api.tupleorerror.FunctionChain.fail;
import static hera.api.tupleorerror.FunctionChain.success;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ResultOrError<T> extends AbstractTupleOrError
    implements ErrorOperation<ResultOrError> {

  public ResultOrError(T result, Throwable error) {
    apply(new Object[] {result});
    setError(error);
  }

  public T getResult() {
    return (T) unapply()[0];
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

}
