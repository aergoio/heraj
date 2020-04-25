/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import static hera.util.ValidationUtils.assertNotNull;
import static hera.util.ValidationUtils.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.Time;
import lombok.Getter;
import org.slf4j.Logger;

class TryCountAndInterval {

  protected final Logger logger = getLogger(getClass());

  /**
   * Create TryCountAndInterval instance.
   *
   * @param tryCount a try count
   * @param interval an try interval
   * @return an TryCountAndInterval instance
   */
  public static TryCountAndInterval of(final int tryCount, final Time interval) {
    return new TryCountAndInterval(tryCount, interval);
  }

  @Getter
  protected final int count;

  @Getter
  protected final Time interval;

  private TryCountAndInterval(final int tryCount, final Time interval) {
    assertTrue(tryCount >= 0, "Try count must be >= 0");
    assertNotNull(interval, "Interval hash must not null");
    this.count = tryCount;
    this.interval = interval;
  }

}
