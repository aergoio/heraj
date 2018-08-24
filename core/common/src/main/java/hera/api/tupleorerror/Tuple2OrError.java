/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

import static hera.api.tupleorerror.FunctionChain.fail;
import static hera.api.tupleorerror.FunctionChain.success;

@SuppressWarnings("unchecked")
public class Tuple2OrError<T0, T1> extends AbstractTupleOrError {

  public Tuple2OrError(T0 t0, T1 t1) {
    apply(new Object[] {t0, t1});
  }

  public Tuple2OrError(Throwable error) {
    setError(error);
  }

  public T0 getT0() {
    return (T0) unapply()[0];
  }

  public T1 getT1() {
    return (T1) unapply()[1];
  }

  /**
   * Apply function to tuple0, tuple1 if no error. Otherwise, don't apply and just keeping error.
   *
   * @param <R> type of applied result
   * @param fn function to apply
   * @return {@code ResultOrError} with values as result of fn
   */
  public <R> ResultOrError<R> thenApply(Function2<T0, T1, R> fn) {
    if (!hasError()) {
      try {
        R next = fn.apply(getT0(), getT1());
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
