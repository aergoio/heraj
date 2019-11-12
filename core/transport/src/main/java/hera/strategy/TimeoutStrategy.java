/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.function.Function;
import hera.api.function.Functions;
import hera.api.model.internal.Time;
import hera.client.internal.HerajFutures;
import hera.exception.DecoratorChainException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.slf4j.Logger;

@ToString(exclude = "logger")
@EqualsAndHashCode(callSuper = false)
public class TimeoutStrategy extends InvocationStrategy {

  protected final Logger logger = getLogger(getClass());

  protected final Time timeout;

  public TimeoutStrategy(final long timeout) {
    this(timeout, TimeUnit.MILLISECONDS);
  }

  public TimeoutStrategy(final long timeout, final TimeUnit timeUnit) {
    this.timeout = Time.of(timeout < 0 ? 0 : timeout, timeUnit);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected <R> R wrap(final Function<R> f, final List<Object> args) {
    try {
      final R shouldBeFuture = Functions.invoke(f, args);
      if (!(shouldBeFuture instanceof Future)) {
        // TODO: handle <R> if isn't future
        throw new UnsupportedOperationException("Return type of function must be future");
      }

      final Future<?> future = (Future<?>) shouldBeFuture;
      final Object ret = future.get(timeout.getValue(), timeout.getUnit());
      return (R) HerajFutures.success(ret);
    } catch (ExecutionException e) {
      throw new DecoratorChainException(e.getCause());
    } catch (InterruptedException | TimeoutException e) {
      if (e instanceof TimeoutException) {
        logger.info("Request timed out with timeout: {}", timeout);
      }
      throw new DecoratorChainException(e);
    } catch (Exception e) {
      throw new DecoratorChainException(e);
    }
  }

}
