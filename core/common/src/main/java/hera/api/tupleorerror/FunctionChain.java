/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

import hera.api.tupleorerror.impl.ResultOrErrorImpl;
import hera.api.tupleorerror.impl.Tuple2OrErrorImpl;
import hera.api.tupleorerror.impl.Tuple3OrErrorImpl;
import hera.api.tupleorerror.impl.Tuple4OrErrorImpl;
import hera.util.DangerousSupplier;
import java.util.function.Supplier;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class FunctionChain {

  /**
   * Make a sequence of ResultOrErrors.
   *
   * @param <T1> type of 1st element of tuple
   * @param <T2> type of 2nd element of tuple
   * @param f1 {@code ResultOrError} supplier
   * @param f2 {@code ResultOrError} supplier
   * @return sequence of result or error
   */
  public static <T1, T2> Tuple2OrError<T1, T2> seq(Supplier<ResultOrError<T1>> f1,
      Supplier<ResultOrError<T2>> f2) {
    ResultOrErrorFuture future1 = ResultOrErrorFutureFactory.supply(f1);
    ResultOrErrorFuture future2 = ResultOrErrorFutureFactory.supply(f2);

    Tuple2OrErrorFuture<T1, T2> future =
        hera.api.tupleorerror.FutureFunctionChain.seq(() -> future1, () -> future2);
    return future.get();
  }

  /**
   * Make a sequence of ResultOrErrors.
   *
   * @param <T1> type of 1st element of tuple
   * @param <T2> type of 2nd element of tuple
   * @param <T3> type of 3rd element of tuple
   * @param f1 {@code ResultOrError} supplier
   * @param f2 {@code ResultOrError} supplier
   * @param f3 {@code ResultOrError} supplier
   * @return sequence of result or error
   */
  public static <T1, T2, T3> Tuple3OrError<T1, T2, T3> seq(Supplier<ResultOrError<T1>> f1,
      Supplier<ResultOrError<T2>> f2, Supplier<ResultOrError<T3>> f3) {
    ResultOrErrorFuture future1 = ResultOrErrorFutureFactory.supply(f1);
    ResultOrErrorFuture future2 = ResultOrErrorFutureFactory.supply(f2);
    ResultOrErrorFuture future3 = ResultOrErrorFutureFactory.supply(f3);

    Tuple3OrErrorFuture<T1, T2, T3> future =
        hera.api.tupleorerror.FutureFunctionChain.seq(() -> future1, () -> future2, () -> future3);
    return future.get();
  }

  /**
   * Make a sequence of ResultOrErrors.
   *
   * @param <T1> type of 1st element of tuple
   * @param <T2> type of 2nd element of tuple
   * @param <T3> type of 3rd element of tuple
   * @param <T4> type of 4th element of tuple
   * @param f1 {@code ResultOrError} supplier
   * @param f2 {@code ResultOrError} supplier
   * @param f3 {@code ResultOrError} supplier
   * @param f4 {@code ResultOrError} supplier
   * @return sequence of result or error
   */
  public static <T1, T2, T3, T4> Tuple4OrError<T1, T2, T3, T4> seq(Supplier<ResultOrError<T1>> f1,
      Supplier<ResultOrError<T2>> f2, Supplier<ResultOrError<T3>> f3,
      Supplier<ResultOrError<T4>> f4) {
    ResultOrErrorFuture future1 = ResultOrErrorFutureFactory.supply(f1);
    ResultOrErrorFuture future2 = ResultOrErrorFutureFactory.supply(f2);
    ResultOrErrorFuture future3 = ResultOrErrorFutureFactory.supply(f3);
    ResultOrErrorFuture future4 = ResultOrErrorFutureFactory.supply(f4);

    Tuple4OrErrorFuture<T1, T2, T3, T4> future = hera.api.tupleorerror.FutureFunctionChain
        .seq(() -> future1, () -> future2, () -> future3, () -> future4);
    return future.get();
  }

  /**
   * Make {@code ResultOrError}. If an {@code supplier} completed without error,
   * {@code ResultOrError} holds a result. Otherwise, it holds error.
   *
   * @param <T> type of result
   * @param supplier result supplier
   * @return supply result
   */
  public static <T> ResultOrError<T> of(final DangerousSupplier<T> supplier) {
    try {
      return success(supplier.get());
    } catch (Exception e) {
      return fail(e);
    }
  }

  public static <T> ResultOrError<T> success(T t) {
    return new ResultOrErrorImpl<>(t, null);
  }

  public static <T1, T2> Tuple2OrError<T1, T2> success(T1 t1, T2 t2) {
    return new Tuple2OrErrorImpl<>(t1, t2);
  }

  public static <T1, T2, T3> Tuple3OrError<T1, T2, T3> success(T1 t1, T2 t2, T3 t3) {
    return new Tuple3OrErrorImpl<>(t1, t2, t3);
  }

  public static <T1, T2, T3, T4> Tuple4OrError<T1, T2, T3, T4> success(T1 t1, T2 t2, T3 t3, T4 t4) {
    return new Tuple4OrErrorImpl<>(t1, t2, t3, t4);
  }

  public static ResultOrError fail(Throwable error) {
    return new ResultOrErrorImpl<>(null, error);
  }

}
