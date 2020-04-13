/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model.internal;

import static hera.util.ValidationUtils.assertNotNull;
import static hera.util.ValidationUtils.assertTrue;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ApiAudience.Private
@ApiStability.Unstable
@ToString
@EqualsAndHashCode
public class Time {

  /**
   * Create a time object with a {@code value} and {@link TimeUnit#MILLISECONDS}.
   *
   * @param value time value
   * @return created {@link Time}
   */
  public static Time of(final long value) {
    return new Time(value, TimeUnit.MILLISECONDS);
  }

  /**
   * Create a time object with a {@code value} and {@code unit}.
   *
   * @param value time value
   * @param unit  time unit
   * @return created {@link Time}
   */
  public static Time of(final long value, final TimeUnit unit) {
    return new Time(value, unit);
  }

  @Getter
  protected final long value;

  @Getter
  protected final TimeUnit unit;

  private Time(final long value, final TimeUnit unit) {
    assertTrue(0 <= value, "Value must >= 0");
    assertNotNull(unit, "Unit must not null");
    this.value = value < 0 ? 0 : value;
    this.unit = unit;
  }

  public long toNanoseconds() {
    return unit.toNanos(value);
  }

  public long toMicroSeconds() {
    return unit.toMicros(value);
  }

  public long toMilliseconds() {
    return unit.toMillis(value);
  }

  public long toSeconds() {
    return unit.toSeconds(value);
  }

  public long toMinutes() {
    return unit.toMinutes(value);
  }

  public long toHours() {
    return unit.toHours(value);
  }

  public long toDays() {
    return unit.toDays(value);
  }

}
