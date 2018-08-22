/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@SuppressWarnings({"rawtypes", "unchecked"})
public class Tuple3OrErrorFuture<T0, T1, T2>
    extends AbstractTupleOrErrorFuture<Tuple3OrError<T0, T1, T2>> {

  private Tuple3OrErrorFuture(final CompletableFuture<Tuple3OrError<T0, T1, T2>> deligate) {
    super(deligate);
  }

  public static <U extends CompletableFuture> Tuple3OrErrorFuture withDeligate(
      final Supplier<U> deligateSupplier) {
    return new Tuple3OrErrorFuture(deligateSupplier.get());
  }

  public static <U extends Tuple3OrError> Tuple3OrErrorFuture supply(final Supplier<U> supplier) {
    return new Tuple3OrErrorFuture(CompletableFuture.supplyAsync(supplier));
  }

  public <R> ResultOrErrorFuture<R> thenApply(Function3<T0, T1, T2, R> f) {
    CompletableFuture<ResultOrError<R>> next = getDeligate().thenApply(r -> r.thenApply(f));
    return new ResultOrErrorFuture<R>(next);
  }

}
