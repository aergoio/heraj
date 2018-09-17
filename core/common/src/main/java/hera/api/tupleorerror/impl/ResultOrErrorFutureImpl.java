/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror.impl;

import static hera.api.tupleorerror.FunctionChain.fail;
import static hera.api.tupleorerror.FunctionChain.success;

import hera.api.tupleorerror.Consumer1;
import hera.api.tupleorerror.Function1;
import hera.api.tupleorerror.Predicate1;
import hera.api.tupleorerror.ResultOrError;
import hera.api.tupleorerror.ResultOrErrorFuture;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ResultOrErrorFutureImpl<T> implements ResultOrErrorFuture<T> {

  @Getter
  protected final CompletableFuture<ResultOrError<T>> deligate;

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
  public ResultOrError<T> get() {
    try {
      return deligate.get();
    } catch (Throwable error) {
      return new ResultOrErrorImpl<>(null, error);
    }
  }

  @Override
  public ResultOrError<T> get(long timeout, TimeUnit unit) {
    try {
      return deligate.get(timeout, unit);
    } catch (Throwable error) {
      return new ResultOrErrorImpl<>(null, error);
    }
  }

  @Override
  public boolean complete(ResultOrError<T> resultOrError) {
    return deligate.complete(resultOrError);
  }

  @SuppressWarnings("unchecked")
  @Override
  public ResultOrErrorFuture<Boolean> ifPresent(Consumer1<? super T> consumer) {
    CompletableFuture<ResultOrError<Boolean>> next = deligate.thenComposeAsync(r -> {
      try {
        consumer.accept(r.getResult());
        return CompletableFuture.supplyAsync(() -> success(true));
      } catch (Throwable e) {
        return CompletableFuture.supplyAsync(() -> fail(e));
      }
    });
    return new ResultOrErrorFutureImpl<Boolean>(next);
  }

  @Override
  public ResultOrErrorFuture<T> filter(Predicate1<? super T> predicate) {
    CompletableFuture<ResultOrError<T>> next = deligate.thenApplyAsync(r -> r.filter(predicate));
    return new ResultOrErrorFutureImpl<T>(next);
  }

  @Override
  public <R> ResultOrErrorFuture<R> map(Function1<? super T, ? extends R> f) {
    CompletableFuture<ResultOrError<R>> next = deligate.thenApplyAsync(r -> r.map(f));
    return new ResultOrErrorFutureImpl<R>(next);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <R> ResultOrErrorFuture<R> flatMap(Function1<? super T, ResultOrErrorFuture<R>> f) {
    CompletableFuture<ResultOrError<R>> next = deligate.thenComposeAsync(r -> {
      try {
        return ((ResultOrErrorFutureImpl<R>) f.apply(r.getResult())).deligate;
      } catch (Throwable e) {
        return CompletableFuture.supplyAsync(() -> fail(e));
      }
    });
    return new ResultOrErrorFutureImpl<R>(next);
  }

}
