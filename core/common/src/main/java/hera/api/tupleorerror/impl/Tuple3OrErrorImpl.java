/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror.impl;

import static hera.api.tupleorerror.FunctionChain.fail;
import static hera.api.tupleorerror.FunctionChain.success;

import hera.api.tupleorerror.Consumer3;
import hera.api.tupleorerror.Function3;
import hera.api.tupleorerror.Predicate3;
import hera.api.tupleorerror.ResultOrError;
import hera.api.tupleorerror.Tuple3OrError;
import hera.exception.HerajException;
import java.util.Arrays;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("unchecked")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Tuple3OrErrorImpl<T1, T2, T3> implements Tuple3OrError<T1, T2, T3> {

  protected final T1 v1;

  protected final T2 v2;

  protected final T3 v3;

  @Getter
  protected final Throwable error;

  /**
   * Tuple3OrErrorImpl constructor with values.
   * 
   * @param v1 1st value
   * @param v2 2nd value
   * @param v3 3rd value
   */
  public Tuple3OrErrorImpl(T1 v1, T2 v2, T3 v3) {
    this(v1, v2, v3, null);
  }

  /**
   * Tuple3OrErrorImpl constructor with error.
   * 
   * @param error an error
   */
  public Tuple3OrErrorImpl(Throwable error) {
    this(null, null, null, error);
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
  public T3 get3() {
    if (null == v3) {
      throw error instanceof HerajException ? (HerajException) error : new HerajException(error);
    }
    return v3;
  }

  @Override
  public void ifPresent(Consumer3<? super T1, ? super T2, ? super T3> consumer) {
    if (!hasError()) {
      consumer.accept(v1, v2, v3);
    }
  }

  @Override
  public Tuple3OrError<T1, T2, T3> filter(
      Predicate3<? super T1, ? super T2, ? super T3> predicate) {
    Objects.requireNonNull(predicate);
    if (!hasError()) {
      try {
        return predicate.test(v1, v2, v3) ? this
            : new Tuple3OrErrorImpl<>(new HerajException("No such element matching predicate"));
      } catch (Throwable e) {
        return new Tuple3OrErrorImpl<>(e);
      }
    } else {
      return this;
    }
  }

  @Override
  public <R> ResultOrError<R> map(Function3<? super T1, ? super T2, ? super T3, ? extends R> fn) {
    if (!hasError()) {
      try {
        R next = fn.apply(v1, v2, v3);
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
    if (!(obj instanceof Tuple3OrErrorImpl)) {
      return false;
    }

    Tuple3OrErrorImpl<?, ?, ?> other = (Tuple3OrErrorImpl<?, ?, ?>) obj;
    return Objects.equals(v1, other.v1) && Objects.equals(v2, other.v2)
        && Objects.equals(v3, other.v3) && Objects.equals(error, other.error);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(new Object[] {v1, v2, v3, error});
  }

  @Override
  public String toString() {
    return !hasError() ? String.format("Tuple[%s, %s, %s]", v1, v2, v3)
        : String.format("Error[%s]", error);
  }

}
