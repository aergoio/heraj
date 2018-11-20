/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.Time;
import hera.api.tupleorerror.Function0;
import hera.api.tupleorerror.ResultOrError;
import hera.api.tupleorerror.ResultOrErrorFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.slf4j.Logger;

@ToString
@EqualsAndHashCode
@SuppressWarnings({"unchecked", "rawtypes"})
public class SimpleTimeoutStrategy implements TimeoutStrategy {

  protected final Logger logger = getLogger(getClass());

  protected final Time timeout;

  public SimpleTimeoutStrategy(final long timeout) {
    this(timeout, TimeUnit.MILLISECONDS);
  }

  public SimpleTimeoutStrategy(final long timeout, final TimeUnit timeUnit) {
    this.timeout = Time.of(timeout < 0 ? 0 : timeout, timeUnit);
  }

  @Override
  public <R> R action(Function0<R> f) {
    final R r = f.apply();
    if (r instanceof ResultOrErrorFuture) {
      ResultOrErrorFuture<?> future = (ResultOrErrorFuture<?>) r;
      ResultOrError resultOrError = future.get(timeout.getValue(), timeout.getUnit());
      if (resultOrError.getError() instanceof TimeoutException) {
        logger.info("Attempt timeout after {}", timeout);
        future.complete(resultOrError);
      }
    }
    return r;
  }

}
