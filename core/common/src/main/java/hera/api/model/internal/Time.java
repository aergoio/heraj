/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model.internal;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@ApiAudience.Private
@ApiStability.Unstable
@EqualsAndHashCode
public class Time {

  /**
   * Create a time object with a {@code value} and {@link TimeUnit#MICROSECONDS}.
   *
   * @param value time value
   * @return created {@link Time}
   */
  public static Time of(final long value) {
    return new Time(value, TimeUnit.MICROSECONDS);
  }

  /**
   * Create a time object with a {@code value} and {@code unit}.
   *
   * @param value time value
   * @param unit time unit
   * @return created {@link Time}
   */
  public static Time of(final long value, final TimeUnit unit) {
    return new Time(value, unit);
  }

  @Getter
  protected final long value;

  @Getter
  protected final TimeUnit unit;

  public Time(long value, TimeUnit unit) {
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

  @Override
  public String toString() {
    return String.format("%d %s", value, unit.toString());
  }

}
