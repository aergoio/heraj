/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@SuppressWarnings({"rawtypes", "unchecked"})
public class Tuple4OrErrorFuture<T0, T1, T2, T3>
    extends AbstractTupleOrErrorFuture<Tuple4OrError<T0, T1, T2, T3>> {

  private Tuple4OrErrorFuture(final CompletableFuture<Tuple4OrError<T0, T1, T2, T3>> deligate) {
    super(deligate);
  }

  public static <U extends CompletableFuture> Tuple4OrErrorFuture withDeligate(
      final Supplier<U> deligateSupplier) {
    return new Tuple4OrErrorFuture(deligateSupplier.get());
  }

  public static <U extends Tuple4OrError> Tuple4OrErrorFuture supply(final Supplier<U> supplier) {
    return new Tuple4OrErrorFuture(CompletableFuture.supplyAsync(supplier));
  }

  public <R> ResultOrErrorFuture<R> thenApply(Function4<T0, T1, T2, T3, R> f) {
    CompletableFuture<ResultOrError<R>> next = getDeligate().thenApply(r -> r.thenApply(f));
    return new ResultOrErrorFuture<R>(next);
  }

}
