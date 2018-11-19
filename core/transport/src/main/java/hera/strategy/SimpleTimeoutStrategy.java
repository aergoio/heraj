/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.api.model.Time;
import hera.api.tupleorerror.Function0;
import hera.api.tupleorerror.ResultOrError;
import hera.api.tupleorerror.ResultOrErrorFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.Getter;

@SuppressWarnings({"unchecked", "rawtypes"})
public class SimpleTimeoutStrategy implements TimeoutStrategy {

  @Getter
  protected final Time timeout;

  public SimpleTimeoutStrategy(final long timeout) {
    this(timeout, TimeUnit.MILLISECONDS);
  }

  public SimpleTimeoutStrategy(final long timeout, final TimeUnit timeUnit) {
    this.timeout = Time.of(timeout < 0 ? 0 : timeout, timeUnit);
  }

  @Override
  public <R> R apply(Function0<R> f) {
    final R r = f.apply();
    if (r instanceof ResultOrErrorFuture) {
      ResultOrErrorFuture<?> future = (ResultOrErrorFuture<?>) r;
      ResultOrError resultOrError = future.get(timeout.getValue(), timeout.getUnit());
      if (resultOrError.getError() instanceof TimeoutException) {
        future.complete(resultOrError);
      }
    }
    return r;
  }

}
