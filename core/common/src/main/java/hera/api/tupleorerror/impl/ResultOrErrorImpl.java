/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror.impl;

import static hera.api.tupleorerror.FunctionChain.fail;
import static hera.api.tupleorerror.FunctionChain.success;

import hera.api.tupleorerror.Consumer1;
import hera.api.tupleorerror.Function1;
import hera.api.tupleorerror.Predicate1;
import hera.api.tupleorerror.ResultOrError;
import hera.exception.HerajException;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("unchecked")
@RequiredArgsConstructor
public class ResultOrErrorImpl<T> implements ResultOrError<T> {

  protected final T result;

  @Getter
  protected final Throwable error;

  @Override
  public T getResult() {
    if (hasError()) {
      throwError();
    }
    return result;
  }

  @Override
  public void ifPresent(Consumer1<? super T> consumer) {
    if (hasResult()) {
      consumer.accept(result);
    }
  }

  @Override
  public ResultOrError<T> filter(Predicate1<? super T> predicate) {
    Objects.requireNonNull(predicate);
    if (hasResult()) {
      try {
        return predicate.test(result) ? this
            : fail(new HerajException("No such element matching predicate"));
      } catch (Throwable e) {
        return fail(e);
      }
    } else {
      return this;
    }
  }


  @Override
  public <R> ResultOrError<R> map(Function1<? super T, ? extends R> fn) {
    if (hasResult()) {
      try {
        R next = fn.apply(result);
        return success(next);
      } catch (Throwable e) {
        return fail(e);
      }
    }
    return fail(getError());
  }

  @Override
  public <R> ResultOrError<R> flatMap(Function1<? super T, ResultOrError<R>> fn) {
    if (hasResult()) {
      try {
        return fn.apply(result);
      } catch (Throwable e) {
        return fail(e);
      }
    }
    return fail(getError());
  }

  @Override
  public T orElse(T other) {
    return hasResult() ? result : other;
  }

  @Override
  public T orElseGet(Supplier<? extends T> other) {
    return hasResult() ? result : other.get();
  }

  @Override
  public <X extends Throwable> T getOrThrows(Supplier<? extends X> exceptionSupplier) throws X {
    if (hasError()) {
      throw exceptionSupplier.get();
    }
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ResultOrErrorImpl)) {
      return false;
    }

    ResultOrErrorImpl<?> other = (ResultOrErrorImpl<?>) obj;
    return Objects.equals(result, other.result) && Objects.equals(error, other.error);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(new Object[] {result, error});
  }

  @Override
  public String toString() {
    return hasResult() ? String.format("Result[%s]", result) : String.format("Error[%s]", error);
  }

}
