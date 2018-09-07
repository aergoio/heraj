/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

import hera.api.tupleorerror.impl.ResultOrErrorImpl;
import hera.api.tupleorerror.impl.Tuple2OrErrorImpl;
import hera.api.tupleorerror.impl.Tuple3OrErrorImpl;
import hera.api.tupleorerror.impl.Tuple4OrErrorImpl;
import java.util.function.Supplier;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class FunctionChain {

  /**
   * Make a sequence of with ResultOrErrors.
   *
   * @param <T0> type of 1st element of tuple
   * @param <T1> type of 2nd element of tuple
   * @param f0 {@code ResultOrError} supplier
   * @param f1 {@code ResultOrError} supplier
   * @return sequence of result or error
   */
  public static <T0, T1> Tuple2OrError<T0, T1> seq(Supplier<ResultOrError<T0>> f0,
      Supplier<ResultOrError<T1>> f1) {
    ResultOrErrorFuture future0 = ResultOrErrorFuture.supply(() -> f0.get());
    ResultOrErrorFuture future1 = ResultOrErrorFuture.supply(() -> f1.get());

    Tuple2OrErrorFuture<T0, T1> future =
        hera.api.tupleorerror.FutureFunctionChain.seq(() -> future0, () -> future1);
    return future.get();
  }

  /**
   * Make a sequence of with ResultOrErrors.
   *
   * @param <T0> type of 1st element of tuple
   * @param <T1> type of 2nd element of tuple
   * @param <T2> type of 3rd element of tuple
   * @param f0 {@code ResultOrError} supplier
   * @param f1 {@code ResultOrError} supplier
   * @param f2 {@code ResultOrError} supplier
   * @return sequence of result or error
   */
  public static <T0, T1, T2> Tuple3OrError<T0, T1, T2> seq(Supplier<ResultOrError<T0>> f0,
      Supplier<ResultOrError<T1>> f1, Supplier<ResultOrError<T2>> f2) {
    ResultOrErrorFuture future0 = ResultOrErrorFuture.supply(() -> f0.get());
    ResultOrErrorFuture future1 = ResultOrErrorFuture.supply(() -> f1.get());
    ResultOrErrorFuture future2 = ResultOrErrorFuture.supply(() -> f2.get());

    Tuple3OrErrorFuture<T0, T1, T2> future =
        hera.api.tupleorerror.FutureFunctionChain.seq(() -> future0, () -> future1, () -> future2);
    return future.get();
  }

  /**
   * Make a sequence of with ResultOrErrors.
   *
   * @param <T0> type of 1st element of tuple
   * @param <T1> type of 2nd element of tuple
   * @param <T2> type of 3rd element of tuple
   * @param <T3> type of 4th element of tuple
   * @param f0 {@code ResultOrError} supplier
   * @param f1 {@code ResultOrError} supplier
   * @param f2 {@code ResultOrError} supplier
   * @param f3 {@code ResultOrError} supplier
   * @return sequence of result or error
   */
  public static <T0, T1, T2, T3> Tuple4OrError<T0, T1, T2, T3> seq(Supplier<ResultOrError<T0>> f0,
      Supplier<ResultOrError<T1>> f1, Supplier<ResultOrError<T2>> f2,
      Supplier<ResultOrError<T3>> f3) {
    ResultOrErrorFuture future0 = ResultOrErrorFuture.supply(() -> f0.get());
    ResultOrErrorFuture future1 = ResultOrErrorFuture.supply(() -> f1.get());
    ResultOrErrorFuture future2 = ResultOrErrorFuture.supply(() -> f2.get());
    ResultOrErrorFuture future3 = ResultOrErrorFuture.supply(() -> f3.get());
    Tuple4OrErrorFuture<T0, T1, T2, T3> future = hera.api.tupleorerror.FutureFunctionChain
        .seq(() -> future0, () -> future1, () -> future2, () -> future3);
    return future.get();
  }


  public static <T> ResultOrError<T> success(T t) {
    return new ResultOrErrorImpl<>(t, null);
  }

  public static <T0, T1> Tuple2OrError<T0, T1> success(T0 t0, T1 t1) {
    return new Tuple2OrErrorImpl<>(t0, t1);
  }

  public static <T0, T1, T2> Tuple3OrError<T0, T1, T2> success(T0 t0, T1 t1, T2 t2) {
    return new Tuple3OrErrorImpl<>(t0, t1, t2);
  }

  public static <T0, T1, T2, T3> Tuple4OrError<T0, T1, T2, T3> success(T0 t0, T1 t1, T2 t2, T3 t3) {
    return new Tuple4OrErrorImpl<>(t0, t1, t2, t3);
  }

  public static ResultOrError fail(Throwable error) {
    return new ResultOrErrorImpl(null, error);
  }

}
