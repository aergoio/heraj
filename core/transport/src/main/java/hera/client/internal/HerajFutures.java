/*
 * @copyright defined in LICENSE.txt
 * 
 */

package hera.client.internal;

import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static hera.util.ValidationUtils.assertNotNull;

import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import hera.api.function.Function1;
import hera.exception.HerajException;
import java.util.concurrent.Future;

public class HerajFutures {

  /**
   * Transform Future of type T into Future of type R.
   * 
   * @param <T> an origin type of future
   * @param <R> a transformed type of future
   * @param origin an origin future
   * @param converter a converter converting T to R
   *
   * @return a converted future
   */
  public static <T, R> Future<R> transform(final Future<T> origin,
      final Function1<T, R> converter) {
    assertNotNull(origin);
    assertNotNull(converter);

    if (!(origin instanceof ListenableFuture)) {
      throw new HerajException("Unsupported future type: " + origin.getClass().getName());
    }

    final ListenableFuture<T> realOrigin = (ListenableFuture<T>) origin;
    final ListenableFuture<R> transformed = Futures.transform(realOrigin, new Function<T, R>() {

      @Override
      public R apply(final T input) {
        return converter.apply(input);
      }
    }, directExecutor());
    return transformed;
  }

  public static <T> Future<T> success(final T value) {
    return Futures.immediateFuture(value);
  }

  public static <T> Future<T> fail(final Throwable throwable) {
    assertNotNull(throwable);
    return Futures.immediateFailedFuture(throwable);
  }

}
