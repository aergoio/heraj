/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.function.Function;
import hera.api.function.Function0;
import hera.api.model.internal.Time;
import hera.api.model.internal.TryCountAndInterval;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.slf4j.Logger;

@ToString
@EqualsAndHashCode(callSuper = false)
public class RetryStrategy extends FailoverStrategy {

  public static final int DEFAULT_RETRY_COUNT = 0;
  public static final long DEFAULT_RETRY_INTERVAL = 5000L; // milliseconds

  protected final Logger logger = getLogger(getClass());

  protected final TryCountAndInterval tryCountAndInterval;

  /**
   * {@code RetryStrategy} constructor. If count is less than
   * {@link RetryStrategy#DEFAULT_RETRY_COUNT}, set as it. An interval set as
   * {@link RetryStrategy#DEFAULT_RETRY_INTERVAL}.
   *
   * @param count retry count
   */
  public RetryStrategy(final int count) {
    this(count, Time.of(DEFAULT_RETRY_INTERVAL, TimeUnit.MILLISECONDS));
  }

  /**
   * {@code RetryStrategy} constructor. If count is less than
   * {@link RetryStrategy#DEFAULT_RETRY_COUNT}, set as it. If interval is null, set as
   * {@link RetryStrategy#DEFAULT_RETRY_INTERVAL}.
   *
   * @param count retry count
   * @param interval retry interval
   */
  public RetryStrategy(final int count, final Time interval) {
    this.tryCountAndInterval =
        TryCountAndInterval.of(count < 0 ? DEFAULT_RETRY_COUNT : count, interval);
  }

  @Override
  public <R> R action(final Function originFunction, final Function0<R> functionWithArgs) {
    R future = functionWithArgs.apply();
    if (future instanceof Future) {
      int i = tryCountAndInterval.getCount();
      boolean success = false;
      try {
        ((Future<?>) future).get();
        success = true;
      } catch (Exception e) {
        // do nothing
      }
      while (!success && 0 < i) {
        try {
          ((Future<?>) future).get();
          success = true;
        } catch (Exception e) {
          logger.info("Attempt failed.. retry after {} milliseconds.. (try left: {})",
              this.tryCountAndInterval.getInterval().toMilliseconds(), i);
          this.tryCountAndInterval.trySleep();
          future = functionWithArgs.apply();
          --i;
        }
      }
    }
    return future;
  }

}
