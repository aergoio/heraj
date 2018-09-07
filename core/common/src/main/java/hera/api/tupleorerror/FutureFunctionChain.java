/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

import static hera.api.tupleorerror.FunctionChain.success;

import hera.api.tupleorerror.impl.Tuple2OrErrorImpl;
import hera.api.tupleorerror.impl.Tuple3OrErrorImpl;
import hera.api.tupleorerror.impl.Tuple4OrErrorImpl;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class FutureFunctionChain {

  /**
   * Make a sequence of future with ResultOrErrorFutures.
   *
   * @param <T0> type of 1st element of tuple
   * @param <T1> type of 2nd element of tuple
   * @param f0 a function suppling future0
   * @param f1 a function suppling future1
   * @return sequence of future
   */
  public static <T0, T1> Tuple2OrErrorFuture<T0, T1> seq(Supplier<ResultOrErrorFuture<T0>> f0,
      Supplier<ResultOrErrorFuture<T1>> f1) {
    try {
      ResultOrErrorFuture<T0> future0 = f0.get();
      ResultOrErrorFuture<T1> future1 = f1.get();

      CompletableFuture<Tuple2OrError> deligate =
          AbstractTupleOrErrorFuture.allOf(future0, future1).thenApply(futureVoid -> {
            try {
              ResultOrError<T0> r0 = future0.get();
              if (r0.hasError()) {
                return new Tuple2OrErrorImpl<>(r0.getError());
              }
              ResultOrError<T1> r1 = future1.get();
              if (r1.hasError()) {
                return new Tuple2OrErrorImpl<>(r1.getError());
              }
              return success(r0.getResult(), r1.getResult());
            } catch (Throwable e) {
              return new Tuple2OrErrorImpl<>(e);
            }
          });
      return Tuple2OrErrorFuture.withDeligate(() -> deligate);
    } catch (Throwable e) {
      return Tuple2OrErrorFuture.supply(() -> new Tuple2OrErrorImpl<>(e));
    }
  }

  /**
   * Make a sequence of future with ResultOrErrorFutures.
   *
   * @param <T0> type of 1st element of tuple
   * @param <T1> type of 2nd element of tuple
   * @param <T2> type of 3rd element of tuple
   * @param f0 a function suppling future0
   * @param f1 a function suppling future1
   * @param f2 a function suppling future2
   * @return sequence of future
   */
  public static <T0, T1, T2> Tuple3OrErrorFuture<T0, T1, T2> seq(
      Supplier<ResultOrErrorFuture<T0>> f0, Supplier<ResultOrErrorFuture<T1>> f1,
      Supplier<ResultOrErrorFuture<T2>> f2) {
    try {
      ResultOrErrorFuture<T0> future0 = f0.get();
      ResultOrErrorFuture<T1> future1 = f1.get();
      ResultOrErrorFuture<T2> future2 = f2.get();

      CompletableFuture<Tuple3OrError> deligate =
          AbstractTupleOrErrorFuture.allOf(future0, future1).thenApply(futureVoid -> {
            try {
              ResultOrError<T0> r0 = future0.get();
              if (r0.hasError()) {
                return new Tuple3OrErrorImpl<>(r0.getError());
              }
              ResultOrError<T1> r1 = future1.get();
              if (r1.hasError()) {
                return new Tuple3OrErrorImpl<>(r1.getError());
              }
              ResultOrError<T2> r2 = future2.get();
              if (r2.hasError()) {
                return new Tuple3OrErrorImpl<>(r2.getError());
              }
              return success(r0.getResult(), r1.getResult(), r2.getResult());
            } catch (Throwable e) {
              return new Tuple3OrErrorImpl<>(e);
            }
          });
      return Tuple3OrErrorFuture.withDeligate(() -> deligate);
    } catch (Throwable e) {
      return Tuple3OrErrorFuture.supply(() -> new Tuple3OrErrorImpl<>(e));
    }
  }

  /**
   * Make a sequence of future with ResultOrErrorFutures.
   *
   * @param <T0> type of 1st element of tuple
   * @param <T1> type of 2nd element of tuple
   * @param <T2> type of 3rd element of tuple
   * @param <T3> type of 4th element of tuple
   * @param f0 a function suppling future0
   * @param f1 a function suppling future1
   * @param f2 a function suppling future2
   * @param f3 a function suppling future3
   * @return sequence of future
   */
  public static <T0, T1, T2, T3> Tuple4OrErrorFuture<T0, T1, T2, T3> seq(
      Supplier<ResultOrErrorFuture<T0>> f0, Supplier<ResultOrErrorFuture<T1>> f1,
      Supplier<ResultOrErrorFuture<T2>> f2, Supplier<ResultOrErrorFuture<T3>> f3) {
    try {
      ResultOrErrorFuture<T0> future0 = f0.get();
      ResultOrErrorFuture<T1> future1 = f1.get();
      ResultOrErrorFuture<T2> future2 = f2.get();
      ResultOrErrorFuture<T3> future3 = f3.get();

      CompletableFuture<Tuple4OrError> deligate =
          AbstractTupleOrErrorFuture.allOf(future0, future1).thenApply(futureVoid -> {
            try {
              ResultOrError<T0> r0 = future0.get();
              if (r0.hasError()) {
                return new Tuple4OrErrorImpl<>(r0.getError());
              }
              ResultOrError<T1> r1 = future1.get();
              if (r1.hasError()) {
                return new Tuple4OrErrorImpl<>(r1.getError());
              }
              ResultOrError<T2> r2 = future2.get();
              if (r2.hasError()) {
                return new Tuple4OrErrorImpl<>(r2.getError());
              }
              ResultOrError<T3> r3 = future3.get();
              if (r3.hasError()) {
                return new Tuple4OrErrorImpl<>(r3.getError());
              }
              return success(r0.getResult(), r1.getResult(), r2.getResult(), r3.getResult());
            } catch (Throwable e) {
              return new Tuple4OrErrorImpl<>(e);
            }
          });
      return Tuple4OrErrorFuture.withDeligate(() -> deligate);
    } catch (Throwable e) {
      return Tuple4OrErrorFuture.supply(() -> new Tuple4OrErrorImpl<>(e));
    }
  }

}
