/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

import static java.util.stream.Collectors.toList;

import hera.exception.HerajException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractTupleOrErrorFuture<TupleOrErrorT extends AbstractTupleOrError>
    implements Future<TupleOrErrorT> {

  @Getter(value = AccessLevel.PROTECTED)
  protected final CompletableFuture<TupleOrErrorT> deligate;

  protected AbstractTupleOrErrorFuture() {
    this(new CompletableFuture<>());
  }

  /**
   * Returns a new CompletableFuture that is completed when all of the given ResultOrErrorFuture
   * complete.
   * 
   * @see java.util.concurrent.CompletableFuture#allOf
   * @param futures futures to wait
   * @return
   */
  public static CompletableFuture<Void> allOf(ResultOrErrorFuture<?>... futures) {
    final CompletableFuture<Void> completableFuture = CompletableFuture.allOf(Arrays.asList(futures)
        .stream().map(f -> f.deligate).collect(toList()).toArray(new CompletableFuture[] {}));
    return completableFuture;
  }

  public boolean complete(TupleOrErrorT tupleOrError) {
    return getDeligate().complete(tupleOrError);
  }

  @Override
  public boolean cancel(boolean mayInterruptIfRunning) {
    return getDeligate().cancel(mayInterruptIfRunning);
  }

  @Override
  public boolean isCancelled() {
    return getDeligate().isCancelled();
  }

  @Override
  public boolean isDone() {
    return getDeligate().isDone();
  }

  @Override
  public TupleOrErrorT get() {
    try {
      return getDeligate().get();
    } catch (Throwable e) {
      throw new HerajException(e);
    }
  }

  @Override
  public TupleOrErrorT get(long timeout, TimeUnit unit) {
    try {
      return getDeligate().get(timeout, unit);
    } catch (Throwable e) {
      throw new HerajException(e);
    }
  }

}
