/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

import java.util.concurrent.Future;

public interface FinishableFuture<T> extends Future<T> {

  /**
   * Complete an {@code ManageableFuture}.
   *
   * @param t complete value
   * @return complete result
   */
  boolean complete(T t);

  /**
   * {@inheritDoc}
   */
  boolean cancel(boolean mayInterruptIfRunning);

  /**
   * If not already completed, completes this {@link FinishableFuture}. It it's running interrupt
   * it.
   *
   * @return {@code false} if the task could not be cancelled, typically because it has already
   *         completed normally; {@code true} otherwise
   */
  default boolean cancel() {
    return cancel(true);
  }

}
