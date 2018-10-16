/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.api.model.Time;
import java.util.concurrent.TimeUnit;
import lombok.Getter;

public class FutureTimeoutStrategy implements TimeoutStrategy {

  @Getter
  protected final Time timeout;

  public FutureTimeoutStrategy(final long timeout) {
    this(timeout, TimeUnit.MILLISECONDS);
  }

  public FutureTimeoutStrategy(final long timeout, final TimeUnit timeUnit) {
    this.timeout = Time.of(timeout < 0 ? 0 : timeout, timeUnit);
  }

}
