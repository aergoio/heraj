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
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.Tuple4OrError;
import hera.api.tupleorerror.Tuple4OrErrorFuture;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Tuple4OrErrorFutureImpl<T1, T2, T3, T4>
    implements Tuple4OrErrorFuture<T1, T2, T3, T4> {

  @Getter
  protected final CompletableFuture<Tuple4OrError<T1, T2, T3, T4>> deligate;

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
  public Tuple4OrError<T1, T2, T3, T4> get() {
    try {
      return deligate.get();
    } catch (Throwable error) {
      return new Tuple4OrErrorImpl<>(error);
    }
  }

  @Override
  public Tuple4OrError<T1, T2, T3, T4> get(long timeout, TimeUnit unit) {
    try {
      return deligate.get(timeout, unit);
    } catch (Throwable error) {
      return new Tuple4OrErrorImpl<>(error);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public ResultOrErrorFuture<Boolean> ifPresent(
      Consumer4<? super T1, ? super T2, ? super T3, ? super T4> consumer) {
    CompletableFuture<ResultOrError<Boolean>> next = deligate.thenComposeAsync(r -> {
      try {
        consumer.accept(r.get1(), r.get2(), r.get3(), r.get4());
        return CompletableFuture.supplyAsync(() -> success(true));
      } catch (Throwable e) {
        return CompletableFuture.supplyAsync(() -> fail(e));
      }
    });
    return new ResultOrErrorFutureImpl<>(next);
  }

  @Override
  public Tuple4OrErrorFuture<T1, T2, T3, T4> filter(
      Predicate4<? super T1, ? super T2, ? super T3, ? super T4> predicate) {
    CompletableFuture<Tuple4OrError<T1, T2, T3, T4>> next =
        deligate.thenApplyAsync(r -> r.filter(predicate));
    return new Tuple4OrErrorFutureImpl<>(next);
  }

  @Override
  public <R> ResultOrErrorFuture<R> map(
      Function4<? super T1, ? super T2, ? super T3, ? super T4, ? extends R> f) {
    CompletableFuture<ResultOrError<R>> next = deligate.thenApplyAsync(r -> r.map(f));
    return new ResultOrErrorFutureImpl<>(next);
  }

}
