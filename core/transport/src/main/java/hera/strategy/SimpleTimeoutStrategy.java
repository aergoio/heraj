/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.function.Function;
import hera.api.function.Function0;
import hera.api.model.internal.Time;
import hera.client.internal.FinishableFuture;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.slf4j.Logger;

@ToString(exclude = "logger")
@EqualsAndHashCode(callSuper = false)
public class SimpleTimeoutStrategy extends TimeoutStrategy {

  protected final Logger logger = getLogger(getClass());

  protected final Time timeout;

  public SimpleTimeoutStrategy(final long timeout) {
    this(timeout, TimeUnit.MILLISECONDS);
  }

  public SimpleTimeoutStrategy(final long timeout, final TimeUnit timeUnit) {
    this.timeout = Time.of(timeout < 0 ? 0 : timeout, timeUnit);
  }

  @Override
  public <R> R action(final Function originFunction, final Function0<R> functionWithArgs) {
    final R r = functionWithArgs.apply();
    if (r instanceof FinishableFuture) {
      FinishableFuture<?> future = (FinishableFuture<?>) r;
      try {
        future.get(timeout.getValue(), timeout.getUnit());
      } catch (Exception e) {
        logger.info("Attempt timeout after {}", timeout);
        future.fail(e);
      }
    }
    return r;
  }

}
