/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static org.slf4j.LoggerFactory.getLogger;

import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.ListenableFuture;
import hera.exception.RpcException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;

public class FinishableFuture<T> extends AbstractFuture<T>
    implements Future<T>, ListenableFuture<T> {

  protected final Logger logger = getLogger(getClass());

  /**
   * Finish a {@code FinishableFuture} with a value. If it's already finished, return false;
   *
   * @param t complete value
   * @return complete result
   */
  public boolean success(final T t) {
    return super.set(t);
  }

  /**
   * Finish a {@code FinishableFuture} with an error. If it's already finished, return false;
   *
   * @param error complete error value
   * @return complete result
   */
  public boolean fail(final Throwable error) {
    return super.setException(
        error instanceof RpcException ? (RpcException) error : new RpcException(error));
  }

  @Override
  public T get() {
    try {
      return super.get();
    } catch (ExecutionException e) {
      throw e.getCause() instanceof RpcException ? (RpcException) e.getCause()
          : new RpcException(e);
    } catch (InterruptedException e) {
      throw new RpcException(e);
    }
  }

  @Override
  public T get(long timeout, TimeUnit unit) {
    try {
      return super.get(timeout, unit);
    } catch (ExecutionException e) {
      throw e.getCause() instanceof RpcException ? (RpcException) e.getCause()
          : new RpcException(e);
    } catch (InterruptedException | TimeoutException e) {
      throw new RpcException(e);
    }
  }

}
