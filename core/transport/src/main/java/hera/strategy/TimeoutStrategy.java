/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.function.Function;
import hera.api.function.Functions;
import hera.api.model.internal.Time;
import hera.client.internal.FinishableFuture;
import hera.exception.RpcException;
import java.util.List;
import java.util.concurrent.TimeUnit;
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

  @Override
  protected <R> R wrap(Function<R> f, List<Object> args) {
    final R shouldBeFuture = Functions.invoke(f, args);

    if (!(shouldBeFuture instanceof FinishableFuture)) {
      throw new RpcException("Return type of function must be future");
    }

    final FinishableFuture<?> future = (FinishableFuture<?>) shouldBeFuture;
    try {
      future.get(timeout.getValue(), timeout.getUnit());
    } catch (Exception e) {
      logger.info("Attempt timeout after {}", timeout);
      future.fail(e);
    }

    return shouldBeFuture;
  }

}
