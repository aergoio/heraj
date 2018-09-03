/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

import static hera.api.tupleorerror.FunctionChain.fail;
import static hera.api.tupleorerror.FunctionChain.success;

import java.util.function.Supplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("unchecked")
@RequiredArgsConstructor
public class ResultOrErrorImpl<T> implements ResultOrError<T> {

  @Getter
  protected final T result;

  @Getter
  protected final Throwable error;

  @Override
  public <R> ResultOrError<R> map(Function1<T, R> fn) {
    if (hasResult()) {
      try {
        R next = fn.apply(getResult());
        return success(next);
      } catch (Throwable e) {
        return fail(e);
      }
    }
    return fail(getError());
  }

  @Override
  public <R> ResultOrError<R> flatMap(Function1<T, ResultOrError<R>> fn) {
    if (hasResult()) {
      try {
        return fn.apply(getResult());
      } catch (Throwable e) {
        return fail(e);
      }
    }
    return fail(getError());
  }

  @Override
  public <X extends Throwable> T getOrThrows(Supplier<? extends X> exceptionSupplier) throws X {
    if (null == getError()) {
      throw exceptionSupplier.get();
    }
    return getResult();
  }

}
