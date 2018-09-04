/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ResultOrErrorFuture<T> extends AbstractTupleOrErrorFuture<ResultOrError<T>> {

  public ResultOrErrorFuture() {
    super();
  }

  public ResultOrErrorFuture(final CompletableFuture<ResultOrError<T>> deligate) {
    super(deligate);
  }

  public static <U extends CompletableFuture> ResultOrErrorFuture withDeligate(
      final Supplier<U> deligateSupplier) {
    return new ResultOrErrorFuture(deligateSupplier.get());
  }

  public static <U extends ResultOrError> ResultOrErrorFuture supply(final Supplier<U> supplier) {
    return new ResultOrErrorFuture(CompletableFuture.supplyAsync(supplier));
  }

  public <R> ResultOrErrorFuture<R> map(Function1<T, R> f) {
    CompletableFuture<ResultOrError<R>> next = getDeligate().thenApply(r -> r.map(f));
    return new ResultOrErrorFuture<R>(next);
  }

}
