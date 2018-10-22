package hera.api.tupleorerror;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public interface EitherFuture<EitherT> extends Future<EitherT> {

  /**
   *
   * FIXME: Where is EitherFutures
   *
   * If not already completed, completes this {@link EitherFuture} with a
   * {@link CancellationException}. Dependent {@link EitherFutures} that have not already completed
   * will also complete exceptionally, with a {@link CompletionException} caused by this
   * {@link CancellationException}.
   *
   * @param mayInterruptIfRunning this value has no effect in this implementation because interrupts
   *        are not used to control processing.
   *
   * @return {@code true} if this task is now cancelled
   */
  boolean cancel(boolean mayInterruptIfRunning);

  EitherT get();

  EitherT get(long timeout, TimeUnit unit);

}
