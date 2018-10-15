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
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.Tuple3OrError;
import hera.api.tupleorerror.Tuple3OrErrorFuture;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Tuple3OrErrorFutureImpl<T1, T2, T3> implements Tuple3OrErrorFuture<T1, T2, T3> {

  @Getter
  protected final CompletableFuture<Tuple3OrError<T1, T2, T3>> deligate;

  @Override
  public boolean cancel(boolean mayInterruptIfRunning) {
    return deligate.cancel(mayInterruptIfRunning);
  }

  @Override
  public boolean isCancelled() {
    return deligate.isCancelled();
  }

  @Override
  public boolean isDone() {
    return deligate.isDone();
  }

  @Override
  public Tuple3OrError<T1, T2, T3> get() {
    try {
      return deligate.get();
    } catch (Throwable error) {
      return new Tuple3OrErrorImpl<>(error);
    }
  }

  @Override
  public Tuple3OrError<T1, T2, T3> get(long timeout, TimeUnit unit) {
    try {
      return deligate.get(timeout, unit);
    } catch (Throwable error) {
      return new Tuple3OrErrorImpl<>(error);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public ResultOrErrorFuture<Boolean> ifPresent(
      Consumer3<? super T1, ? super T2, ? super T3> consumer) {
    CompletableFuture<ResultOrError<Boolean>> next = deligate.thenComposeAsync(r -> {
      try {
        consumer.accept(r.get1(), r.get2(), r.get3());
        return CompletableFuture.supplyAsync(() -> success(true));
      } catch (Throwable e) {
        return CompletableFuture.supplyAsync(() -> fail(e));
      }
    });
    return new ResultOrErrorFutureImpl<>(next);
  }

  @Override
  public Tuple3OrErrorFuture<T1, T2, T3> filter(
      Predicate3<? super T1, ? super T2, ? super T3> predicate) {
    CompletableFuture<Tuple3OrError<T1, T2, T3>> next =
        deligate.thenApplyAsync(r -> r.filter(predicate));
    return new Tuple3OrErrorFutureImpl<>(next);
  }

  @Override
  public <R> ResultOrErrorFuture<R> map(
      Function3<? super T1, ? super T2, ? super T3, ? extends R> f) {
    CompletableFuture<ResultOrError<R>> next = deligate.thenApplyAsync(r -> r.map(f));
    return new ResultOrErrorFutureImpl<>(next);
  }

}
