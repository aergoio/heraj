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
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.Tuple2OrError;
import hera.api.tupleorerror.Tuple2OrErrorFuture;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Tuple2OrErrorFutureImpl<T1, T2> implements Tuple2OrErrorFuture<T1, T2> {

  @Getter
  protected final CompletableFuture<Tuple2OrError<T1, T2>> deligate;

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
  public Tuple2OrError<T1, T2> get() {
    try {
      return deligate.get();
    } catch (Throwable error) {
      return new Tuple2OrErrorImpl<>(error);
    }
  }

  @Override
  public Tuple2OrError<T1, T2> get(long timeout, TimeUnit unit) {
    try {
      return deligate.get(timeout, unit);
    } catch (Throwable error) {
      return new Tuple2OrErrorImpl<>(error);
    }
  }

  @Override
  public boolean complete(Tuple2OrError<T1, T2> tuple2OrError) {
    return deligate.complete(tuple2OrError);
  }

  @SuppressWarnings("unchecked")
  @Override
  public ResultOrErrorFuture<Boolean> ifPresent(Consumer2<? super T1, ? super T2> consumer) {
    CompletableFuture<ResultOrError<Boolean>> next = deligate.thenComposeAsync(r -> {
      try {
        consumer.accept(r.get1(), r.get2());
        return CompletableFuture.supplyAsync(() -> success(true));
      } catch (Throwable e) {
        return CompletableFuture.supplyAsync(() -> fail(e));
      }
    });
    return new ResultOrErrorFutureImpl<>(next);
  }

  @Override
  public Tuple2OrErrorFuture<T1, T2> filter(Predicate2<? super T1, ? super T2> predicate) {
    CompletableFuture<Tuple2OrError<T1, T2>> next =
        deligate.thenApplyAsync(r -> r.filter(predicate));
    return new Tuple2OrErrorFutureImpl<>(next);
  }

  @Override
  public <R> ResultOrErrorFuture<R> map(Function2<? super T1, ? super T2, ? extends R> f) {
    CompletableFuture<ResultOrError<R>> next = deligate.thenApplyAsync(r -> r.map(f));
    return new ResultOrErrorFutureImpl<>(next);
  }

}
