/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

import static hera.api.tupleorerror.FunctionChain.fail;
import static hera.api.tupleorerror.FunctionChain.success;

import hera.api.tupleorerror.impl.ResultOrErrorFutureImpl;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public final class ResultOrErrorFutureFactory {

  /**
   * Supply an empty {@link ResultOrErrorFuture}.
   *
   * @return an empty {@link ResultOrErrorFuture}
   */
  @SuppressWarnings("rawtypes")
  public static ResultOrErrorFuture supplyEmptyFuture() {
    return new ResultOrErrorFutureImpl<>(new CompletableFuture<>());
  }

  /**
   * Supply {@link ResultOrErrorFuture} with a plain supplier.
   *
   * @param <T> data type
   * @param supplier a provider
   * @return {@link ResultOrErrorFuture}
   */
  @SuppressWarnings("unchecked")
  public static <T> ResultOrErrorFuture<T> supply(final Supplier<T> supplier) {
    return new ResultOrErrorFutureImpl<T>(CompletableFuture.supplyAsync(() -> {
      try {
        return success(supplier.get());
      } catch (Exception e) {
        return fail(e);
      }
    }));
  }

  /**
   * Supply {@link ResultOrErrorFuture} with a plain supplier and task executor.
   *
   * @param <T> data type
   * @param supplier a provider
   * @param executor a task executor
   * @return {@link ResultOrErrorFuture}
   */
  @SuppressWarnings("unchecked")
  public static <T> ResultOrErrorFuture<T> supply(final Supplier<T> supplier,
      final Executor executor) {
    return new ResultOrErrorFutureImpl<T>(CompletableFuture.supplyAsync(() -> {
      try {
        return success(supplier.get());
      } catch (Exception e) {
        return fail(e);
      }
    }, executor));
  }

}
