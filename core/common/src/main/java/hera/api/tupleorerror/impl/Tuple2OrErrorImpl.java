/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror.impl;

import static hera.api.tupleorerror.FunctionChain.fail;
import static hera.api.tupleorerror.FunctionChain.success;

import hera.api.tupleorerror.Consumer2;
import hera.api.tupleorerror.Function2;
import hera.api.tupleorerror.Predicate2;
import hera.api.tupleorerror.ResultOrError;
import hera.api.tupleorerror.Tuple2OrError;
import hera.exception.HerajException;
import java.util.Arrays;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("unchecked")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class Tuple2OrErrorImpl<T1, T2> implements Tuple2OrError<T1, T2> {

  protected final T1 v1;

  protected final T2 v2;

  @Getter
  protected final Throwable error;

  /**
   * Tuple2OrErrorImpl constructor with values.
   * 
   * @param v1 1st value
   * @param v2 2nd value
   */
  public Tuple2OrErrorImpl(T1 v1, T2 v2) {
    this(v1, v2, null);
  }

  /**
   * Tuple2OrErrorImpl constructor with error.
   * 
   * @param error an error
   */
  public Tuple2OrErrorImpl(Throwable error) {
    this(null, null, error);
  }

  @Override
  public T1 get1() {
    if (null == v1) {
      throw error instanceof HerajException ? (HerajException) error : new HerajException(error);
    }
    return v1;
  }

  @Override
  public T2 get2() {
    if (null == v2) {
      throw error instanceof HerajException ? (HerajException) error : new HerajException(error);
    }
    return v2;
  }

  @Override
  public void ifPresent(Consumer2<? super T1, ? super T2> consumer) {
    if (!hasError()) {
      consumer.accept(v1, v2);
    }
  }

  @Override
  public Tuple2OrError<T1, T2> filter(Predicate2<? super T1, ? super T2> predicate) {
    Objects.requireNonNull(predicate);
    if (!hasError()) {
      return predicate.test(v1, v2) ? this
          : new Tuple2OrErrorImpl<>(new HerajException("No such element matching predicate"));
    } else {
      return this;
    }
  }


  @Override
  public <R> ResultOrError<R> map(Function2<? super T1, ? super T2, ? extends R> fn) {
    if (!hasError()) {
      try {
        R next = fn.apply(v1, v2);
        return success(next);
      } catch (Throwable e) {
        return fail(e);
      }
    }
    return fail(getError());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Tuple2OrErrorImpl)) {
      return false;
    }

    Tuple2OrErrorImpl<?, ?> other = (Tuple2OrErrorImpl<?, ?>) obj;
    return Objects.equals(v1, other.v1) && Objects.equals(v2, other.v2)
        && Objects.equals(error, other.error);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(new Object[] {v1, v2, error});
  }

  @Override
  public String toString() {
    return !hasError() ? String.format("Tuple[%s, %s]", v1, v2) : String.format("Error[%s]", error);
  }

}
