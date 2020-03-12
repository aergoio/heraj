/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import static hera.util.ValidationUtils.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.function.Function;
import hera.api.function.Function0;
import hera.api.function.Functions;
import hera.api.model.internal.Time;
import hera.exception.DecoratorChainException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.slf4j.Logger;

@ToString
@EqualsAndHashCode(callSuper = false)
public class JustRetryStrategy extends FailoverStrategy {

  public static final long DEFAULT_RETRY_INTERVAL = 500L; // milliseconds

  @ToString.Exclude
  protected final transient Logger logger = getLogger(getClass());

  protected final int count;

  protected final long interval; // milliseconds

  public JustRetryStrategy(final int count) {
    this(count, Time.of(DEFAULT_RETRY_INTERVAL, TimeUnit.MILLISECONDS));
  }

  /**
   * {@code RetryStrategy} constructor.
   *
   * @param count    retry count. must be positive
   * @param interval retry interval
   */
  public JustRetryStrategy(final int count, final Time interval) {
    assertTrue(0 < count, "Retry count must be positive");
    this.count = count;
    this.interval = interval.toMilliseconds();
  }

  @Override
  protected <R> R onFailure(final Exception e, final Function<R> f, final List<Object> args) {
    logger.debug("First attempt failed with {}", extractExactCause(e).toString());

    R ret = null;

    final Function0<R> invocation = Functions.buildInvocation(f, args);

    Exception recentException = null;
    int countDown = this.count;
    boolean success = false;
    while (0 < countDown && !success) {
      try {
        // wait
        --countDown;
        Thread.sleep(this.interval);

        // try
        ret = invocation.apply();

        success = true;
      } catch (Exception retryError) {
        recentException = retryError;
        logger.info(
            "Retry failed.. retry with same args after {} milliseconds (try left: {}) cause: {}",
            this.interval, countDown, extractExactCause(retryError).toString());
      }
    }

    // failed even retry
    if (null == ret) {
      throw new DecoratorChainException(recentException);
    }

    return ret;
  }

  protected Throwable extractExactCause(final Exception e) {
    return (e instanceof DecoratorChainException) ? e.getCause() : e;
  }

}
