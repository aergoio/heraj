/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;
import static hera.util.ValidationUtils.assertTrue;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ApiAudience.Public
@ApiStability.Unstable
@Getter
@ToString
@EqualsAndHashCode
public class TryCountAndInterval {

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

  protected final int count;

  protected final Time interval;

  private TryCountAndInterval(final int tryCount, final Time interval) {
    assertTrue(1 <= tryCount, "Try count must >= 1");
    assertNotNull(interval, "Interval must not null");
    assertTrue(0 < interval.getValue(), "Interval value must > 0");
    this.count = tryCount;
    this.interval = interval;
  }

}
