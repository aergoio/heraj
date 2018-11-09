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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class Tuple3OrErrorFutureImpl<T1, T2, T3> implements Tuple3OrErrorFuture<T1, T2, T3> {

  @Getter
  protected final CompletableFuture<Tuple3OrError<T1, T2, T3>> delegate;

  @Override
  public boolean cancel(boolean mayInterruptIfRunning) {
    return delegate.cancel(mayInterruptIfRunning);
  }

  @Override
  public boolean isCancelled() {
    return delegate.isCancelled();
  }

  @Override
  public boolean isDone() {
    return delegate.isDone();
  }

  @Override
  public Tuple3OrError<T1, T2, T3> get() {
    try {
      return delegate.get();
    } catch (Throwable error) {
      return new Tuple3OrErrorImpl<>(error);
    }
  }

  @Override
  public Tuple3OrError<T1, T2, T3> get(long timeout, TimeUnit unit) {
    try {
      return delegate.get(timeout, unit);
    } catch (Throwable error) {
      return new Tuple3OrErrorImpl<>(error);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public ResultOrErrorFuture<Boolean> ifPresent(
      Consumer3<? super T1, ? super T2, ? super T3> consumer) {
    CompletableFuture<ResultOrError<Boolean>> next = delegate.thenComposeAsync(r -> {
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
        delegate.thenApplyAsync(r -> r.filter(predicate));
    return new Tuple3OrErrorFutureImpl<>(next);
  }

  @Override
  public <R> ResultOrErrorFuture<R> map(
      Function3<? super T1, ? super T2, ? super T3, ? extends R> f) {
    CompletableFuture<ResultOrError<R>> next = delegate.thenApplyAsync(r -> r.map(f));
    return new ResultOrErrorFutureImpl<>(next);
  }

}
