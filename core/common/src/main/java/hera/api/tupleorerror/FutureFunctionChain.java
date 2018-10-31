/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

import static hera.api.tupleorerror.FunctionChain.success;

import hera.api.tupleorerror.impl.ResultOrErrorFutureImpl;
import hera.api.tupleorerror.impl.Tuple2OrErrorFutureImpl;
import hera.api.tupleorerror.impl.Tuple2OrErrorImpl;
import hera.api.tupleorerror.impl.Tuple3OrErrorFutureImpl;
import hera.api.tupleorerror.impl.Tuple3OrErrorImpl;
import hera.api.tupleorerror.impl.Tuple4OrErrorFutureImpl;
import hera.api.tupleorerror.impl.Tuple4OrErrorImpl;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class FutureFunctionChain {
  /**
   * Returns a new CompletableFuture that is completed when all of the given ResultOrErrorFuture
   * complete.
   * 
   * @see java.util.concurrent.CompletableFuture#allOf
   * @param futures futures to wait
   * @return a new CompletableFuture that is completed when all of the given CompletableFutures
   *         complete
   */
  protected static CompletableFuture<Void> allOf(ResultOrErrorFuture<?>... futures) {
    final CompletableFuture<Void> next = CompletableFuture.allOf(Arrays.stream(futures)
        .map(ResultOrErrorFutureImpl.class::cast).map(f -> f.getDelegate())
        .toArray(CompletableFuture[]::new));
    return next;
  }

  /**
   * Make a sequence of future with ResultOrErrorFutures.
   *
   * @param <T1> type of 1st element of tuple
   * @param <T2> type of 2nd element of tuple
   * @param f1 a function suppling future0
   * @param f2 a function suppling future1
   * @return sequence of future
   */
  public static <T1, T2> Tuple2OrErrorFuture<T1, T2> seq(Supplier<ResultOrErrorFuture<T1>> f1,
      Supplier<ResultOrErrorFuture<T2>> f2) {
    ResultOrErrorFuture<T1> future1 = f1.get();
    ResultOrErrorFuture<T2> future2 = f2.get();

    CompletableFuture<Tuple2OrError<T1, T2>> delegate =
        allOf(future1, future2).thenApplyAsync(futureVoid -> {
          try {
            ResultOrError<T1> r1 = future1.get();
            if (r1.hasError()) {
              return new Tuple2OrErrorImpl<>(r1.getError());
            }
            ResultOrError<T2> r2 = future2.get();
            if (r2.hasError()) {
              return new Tuple2OrErrorImpl<>(r2.getError());
            }
            return success(r1.getResult(), r2.getResult());
          } catch (Throwable e) {
            return new Tuple2OrErrorImpl<>(e);
          }
        });
    return new Tuple2OrErrorFutureImpl(delegate);
  }

  /**
   * Make a sequence of future with ResultOrErrorFutures.
   *
   * @param <T1> type of 1st element of tuple
   * @param <T2> type of 2nd element of tuple
   * @param <T3> type of 3rd element of tuple
   * @param f1 a function suppling future0
   * @param f2 a function suppling future1
   * @param f3 a function suppling future2
   * @return sequence of future
   */
  public static <T1, T2, T3> Tuple3OrErrorFuture<T1, T2, T3> seq(
      Supplier<ResultOrErrorFuture<T1>> f1, Supplier<ResultOrErrorFuture<T2>> f2,
      Supplier<ResultOrErrorFuture<T3>> f3) {
    ResultOrErrorFuture<T1> future1 = f1.get();
    ResultOrErrorFuture<T2> future2 = f2.get();
    ResultOrErrorFuture<T3> future3 = f3.get();

    CompletableFuture<Tuple3OrError> delegate =
        allOf(future1, future2, future3).thenApplyAsync(futureVoid -> {
          try {
            ResultOrError<T1> r1 = future1.get();
            if (r1.hasError()) {
              return new Tuple3OrErrorImpl<>(r1.getError());
            }
            ResultOrError<T2> r2 = future2.get();
            if (r2.hasError()) {
              return new Tuple3OrErrorImpl<>(r2.getError());
            }
            ResultOrError<T3> r3 = future3.get();
            if (r3.hasError()) {
              return new Tuple3OrErrorImpl<>(r3.getError());
            }
            return success(r1.getResult(), r2.getResult(), r3.getResult());
          } catch (Throwable e) {
            return new Tuple3OrErrorImpl<>(e);
          }
        });
    return new Tuple3OrErrorFutureImpl(delegate);
  }

  /**
   * Make a sequence of future with ResultOrErrorFutures.
   *
   * @param <T1> type of 1st element of tuple
   * @param <T2> type of 2nd element of tuple
   * @param <T3> type of 3rd element of tuple
   * @param <T4> type of 4th element of tuple
   * @param f1 a function suppling future0
   * @param f2 a function suppling future1
   * @param f3 a function suppling future2
   * @param f4 a function suppling future3
   * @return sequence of future
   */
  public static <T1, T2, T3, T4> Tuple4OrErrorFuture<T1, T2, T3, T4> seq(
      Supplier<ResultOrErrorFuture<T1>> f1, Supplier<ResultOrErrorFuture<T2>> f2,
      Supplier<ResultOrErrorFuture<T3>> f3, Supplier<ResultOrErrorFuture<T4>> f4) {
    ResultOrErrorFuture<T1> future1 = f1.get();
    ResultOrErrorFuture<T2> future2 = f2.get();
    ResultOrErrorFuture<T3> future3 = f3.get();
    ResultOrErrorFuture<T4> future4 = f4.get();

    CompletableFuture<Tuple4OrError> delegate =
        allOf(future1, future2).thenApplyAsync(futureVoid -> {
          try {
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
            ResultOrError<T4> r4 = future4.get();
            if (r4.hasError()) {
              return new Tuple4OrErrorImpl<>(r4.getError());
            }
            return success(r1.getResult(), r2.getResult(), r3.getResult(), r4.getResult());
          } catch (Throwable e) {
            return new Tuple4OrErrorImpl<>(e);
          }
        });
    return new Tuple4OrErrorFutureImpl(delegate);
  }

}
