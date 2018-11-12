/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.tupleorerror;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public interface ErrorHoldableFuture<T> extends Future<T> {

  /**
   * Waits if necessary for the computation to complete, and then retrieves its result. Unlike
   * {@link Future}, it can hold an error so it's not throw exception.
   *
   * @return the computed result
   */
  T get();

  /**
   * Waits if necessary for at most the given time for the computation to complete, and then
   * retrieves its result, if available. Unlike {@link Future}, it can hold an error so it's not
   * throw exception.
   *
   * @param timeout the maximum time to wait
   * @param unit the time unit of the timeout argument
   * @return the computed result
   */
  T get(long timeout, TimeUnit unit);

}
