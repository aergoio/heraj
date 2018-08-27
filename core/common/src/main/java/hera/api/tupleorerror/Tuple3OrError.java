/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

import static hera.api.tupleorerror.FunctionChain.fail;
import static hera.api.tupleorerror.FunctionChain.success;

@SuppressWarnings({"unchecked", "rawtypes"})
public class Tuple3OrError<T0, T1, T2> extends AbstractTupleOrError
    implements ErrorOperation<Tuple3OrError> {

  public Tuple3OrError(T0 t0, T1 t1, T2 t2) {
    apply(new Object[] {t0, t1, t2});
  }

  public Tuple3OrError(Throwable error) {
    setError(error);
  }

  public T0 getT0() {
    return !getValues().isEmpty() ? (T0) unapply()[0] : null;
  }

  public T1 getT1() {
    return !getValues().isEmpty() ? (T1) unapply()[1] : null;
  }

  public T2 getT2() {
    return !getValues().isEmpty() ? (T2) unapply()[2] : null;
  }

  /**
   * Apply function to tuple0, tuple1, tuple2 if no error. Otherwise, don't apply and just keeping
   * error.
   *
   * @param <R> type of applied result
   * @param fn function to apply
   * @return {@code ResultOrError} with values as result of fn
   */
  public <R> ResultOrError<R> thenApply(Function3<T0, T1, T2, R> fn) {
    if (!hasError()) {
      try {
        R next = fn.apply(getT0(), getT1(), getT2());
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
