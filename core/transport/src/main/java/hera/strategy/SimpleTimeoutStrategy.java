/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.api.model.Time;
import java.util.concurrent.TimeUnit;
import lombok.Getter;

public class SimpleTimeoutStrategy implements TimeoutStrategy {

  @Getter
  protected final Time timeout;

  public SimpleTimeoutStrategy(final long timeout) {
    this(timeout, TimeUnit.MILLISECONDS);
  }

  public SimpleTimeoutStrategy(final long timeout, final TimeUnit timeUnit) {
    this.timeout = Time.of(timeout < 0 ? 0 : timeout, timeUnit);
  }

}
