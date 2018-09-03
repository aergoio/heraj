/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@SuppressWarnings({"rawtypes", "unchecked"})
public class Tuple2OrErrorFuture<T0, T1> extends AbstractTupleOrErrorFuture<Tuple2OrError<T0, T1>> {

  private Tuple2OrErrorFuture(final CompletableFuture<Tuple2OrError<T0, T1>> deligate) {
    super(deligate);
  }

  public static <U extends CompletableFuture> Tuple2OrErrorFuture withDeligate(
      final Supplier<U> deligateSupplier) {
    return new Tuple2OrErrorFuture(deligateSupplier.get());
  }

  public static <U extends Tuple2OrError> Tuple2OrErrorFuture supply(final Supplier<U> supplier) {
    return new Tuple2OrErrorFuture(CompletableFuture.supplyAsync(supplier));
  }

  public <R> ResultOrErrorFuture<R> map(Function2<T0, T1, R> f) {
    CompletableFuture<ResultOrError<R>> next = getDeligate().thenApply(r -> r.map(f));
    return new ResultOrErrorFuture<R>(next);
  }

}
