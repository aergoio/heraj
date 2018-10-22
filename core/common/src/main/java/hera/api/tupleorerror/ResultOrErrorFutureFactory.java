/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

import hera.api.tupleorerror.impl.ResultOrErrorFutureImpl;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public final class ResultOrErrorFutureFactory {

  @SuppressWarnings("rawtypes")
  public static ResultOrErrorFuture supplyEmptyFuture() {
    return new ResultOrErrorFutureImpl<>(new CompletableFuture<>());
  }

  public static <T> ResultOrErrorFuture<T> supply(final Supplier<ResultOrError<T>> supplier) {
    return new ResultOrErrorFutureImpl<>(CompletableFuture.supplyAsync(supplier));
  }

}
