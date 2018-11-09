/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

import static hera.api.tupleorerror.FunctionChain.success;
import static java.util.stream.Collectors.toList;

import hera.api.tupleorerror.impl.ResultOrErrorFutureImpl;
import hera.api.tupleorerror.impl.Tuple2OrErrorFutureImpl;
import hera.api.tupleorerror.impl.Tuple2OrErrorImpl;
import hera.api.tupleorerror.impl.Tuple3OrErrorFutureImpl;
import hera.api.tupleorerror.impl.Tuple3OrErrorImpl;
import hera.api.tupleorerror.impl.Tuple4OrErrorFutureImpl;
import hera.api.tupleorerror.impl.Tuple4OrErrorImpl;
import hera.exception.HerajException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class FutureFunctionChain {

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
    final CompletableFuture<Tuple2OrError<T1, T2>> delegate = CompletableFuture.supplyAsync(() -> {
      try {
        final List<ResultOrErrorFuture> futures =
            Arrays.asList(new ResultOrErrorFuture[] {f1.get(), f2.get()});
        final List<Object> list = futures.parallelStream()
            .map(f -> ((ResultOrErrorFutureImpl) f).getDelegate().handle((t, e) -> {
              if (null != t && ((ResultOrError) t).hasResult()) {
                return ((ResultOrError) t).getResult();
              } else {
                futures.stream().forEach(ResultOrErrorFuture::cancel);
                final Throwable error =
                    (e == null ? ((ResultOrError) t).getError() : (Throwable) e);
                throw new HerajException(error);
              }
            })).map(CompletableFuture::join).collect(toList());
        return success((T1) list.get(0), (T2) list.get(1));
      } catch (Throwable e) {
        while (null != e.getCause()) {
          e = e.getCause();
        }
        return new Tuple2OrErrorImpl<T1, T2>(e);
      }
    });
    return new Tuple2OrErrorFutureImpl<>(delegate);
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
    final CompletableFuture<Tuple3OrError<T1, T2, T3>> delegate =
        CompletableFuture.supplyAsync(() -> {
          try {
            final List<ResultOrErrorFuture> futures =
                Arrays.asList(new ResultOrErrorFuture[] {f1.get(), f2.get(), f3.get()});
            final List<Object> list = futures.parallelStream()
                .map(f -> ((ResultOrErrorFutureImpl) f).getDelegate().handle((t, e) -> {
                  if (null != t && ((ResultOrError) t).hasResult()) {
                    return ((ResultOrError) t).getResult();
                  } else {
                    futures.stream().forEach(ResultOrErrorFuture::cancel);
                    final Throwable error =
                        (e == null ? ((ResultOrError) t).getError() : (Throwable) e);
                    throw new HerajException(error);
                  }
                })).map(CompletableFuture::join).collect(toList());
            return success((T1) list.get(0), (T2) list.get(1), (T3) list.get(2));
          } catch (Throwable e) {
            while (null != e.getCause()) {
              e = e.getCause();
            }
            return new Tuple3OrErrorImpl<T1, T2, T3>(e);
          }
        });
    return new Tuple3OrErrorFutureImpl<>(delegate);
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
    final CompletableFuture<Tuple4OrError<T1, T2, T3, T4>> delegate =
        CompletableFuture.supplyAsync(() -> {
          try {
            final List<ResultOrErrorFuture> futures =
                Arrays.asList(new ResultOrErrorFuture[] {f1.get(), f2.get(), f3.get(), f4.get()});
            final List<Object> list = futures.parallelStream()
                .map(f -> ((ResultOrErrorFutureImpl) f).getDelegate().handle((t, e) -> {
                  if (null != t && ((ResultOrError) t).hasResult()) {
                    return ((ResultOrError) t).getResult();
                  } else {
                    futures.stream().forEach(ResultOrErrorFuture::cancel);
                    final Throwable error =
                        (e == null ? ((ResultOrError) t).getError() : (Throwable) e);
                    throw new HerajException(error);
                  }
                })).map(CompletableFuture::join).collect(toList());
            return success((T1) list.get(0), (T2) list.get(1), (T3) list.get(2), (T4) list.get(3));
          } catch (Throwable e) {
            while (null != e.getCause()) {
              e = e.getCause();
            }
            return new Tuple4OrErrorImpl<T1, T2, T3, T4>(e);
          }
        });
    return new Tuple4OrErrorFutureImpl<>(delegate);
  }

}
