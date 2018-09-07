/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror.impl;

import static hera.api.tupleorerror.FunctionChain.fail;
import static hera.api.tupleorerror.FunctionChain.success;

import hera.api.tupleorerror.Consumer4;
import hera.api.tupleorerror.Function4;
import hera.api.tupleorerror.Predicate4;
import hera.api.tupleorerror.ResultOrError;
import hera.api.tupleorerror.Tuple4OrError;
import hera.exception.HerajException;
import java.util.Arrays;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("unchecked")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Tuple4OrErrorImpl<T1, T2, T3, T4> implements Tuple4OrError<T1, T2, T3, T4> {

  protected final T1 v1;

  protected final T2 v2;

  protected final T3 v3;

  protected final T4 v4;

  @Getter
  protected final Throwable error;

  /**
   * Tuple4OrErrorImpl constructor with values.
   * 
   * @param v1 1st value
   * @param v2 2nd value
   * @param v3 3rd value
   * @param v4 4th value
   */
  public Tuple4OrErrorImpl(T1 v1, T2 v2, T3 v3, T4 v4) {
    this(v1, v2, v3, v4, null);
  }

  /**
   * Tuple4OrErrorImpl constructor with error.
   * 
   * @param error an error
   */
  public Tuple4OrErrorImpl(Throwable error) {
    this(null, null, null, null, error);
  }

  @Override
  public T1 get1() {
    if (hasError()) {
      throw error instanceof HerajException ? (HerajException) error : new HerajException(error);
    }
    return v1;
  }

  @Override
  public T2 get2() {
    if (hasError()) {
      throw error instanceof HerajException ? (HerajException) error : new HerajException(error);
    }
    return v2;
  }

  @Override
  public T3 get3() {
    if (hasError()) {
      throw error instanceof HerajException ? (HerajException) error : new HerajException(error);
    }
    return v3;
  }

  @Override
  public T4 get4() {
    if (hasError()) {
      throw error instanceof HerajException ? (HerajException) error : new HerajException(error);
    }
    return v4;
  }

  @Override
  public void ifPresent(Consumer4<? super T1, ? super T2, ? super T3, ? super T4> consumer) {
    if (!hasError()) {
      consumer.accept(v1, v2, v3, v4);
    }
  }

  @Override
  public Tuple4OrError<T1, T2, T3, T4> filter(
      Predicate4<? super T1, ? super T2, ? super T3, ? super T4> predicate) {
    Objects.requireNonNull(predicate);
    if (!hasError()) {
      return predicate.test(v1, v2, v3, v4) ? this
          : new Tuple4OrErrorImpl<>(new HerajException("No such element matching predicate"));
    } else {
      return this;
    }
  }


  @Override
  public <R> ResultOrError<R> map(
      Function4<? super T1, ? super T2, ? super T3, ? super T4, ? extends R> fn) {
    if (!hasError()) {
      try {
        R next = fn.apply(v1, v2, v3, v4);
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
    if (!(obj instanceof Tuple4OrErrorImpl)) {
      return false;
    }

    Tuple4OrErrorImpl<?, ?, ?, ?> other = (Tuple4OrErrorImpl<?, ?, ?, ?>) obj;
    return Objects.equals(v1, other.v1) && Objects.equals(v2, other.v2)
        && Objects.equals(v3, other.v3) && Objects.equals(v4, other.v4)
        && Objects.equals(error, other.error);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(new Object[] {v1, v2, v3, v4, error});
  }

  @Override
  public String toString() {
    return !hasError() ? String.format("Tuple[%s, %s, %s, %s]", v1, v2, v3, v4)
        : String.format("Error[%s]", error);
  }

}
