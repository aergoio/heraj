/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;
import org.junit.Test;

public class TimeTest {

  @Test
  public void testConversion() {
    final long baseTimeInMicroSeconds = 100_000_000_000_000_000L;
    final TimeUnit timeUnit = TimeUnit.MICROSECONDS;
    final Time time = Time.of(baseTimeInMicroSeconds);
    assertEquals(timeUnit.toNanos(baseTimeInMicroSeconds), time.toNanoseconds());
    assertEquals(timeUnit.toMicros(baseTimeInMicroSeconds), time.toMicroSeconds());
    assertEquals(timeUnit.toMillis(baseTimeInMicroSeconds), time.toMiliseconds());
    assertEquals(timeUnit.toSeconds(baseTimeInMicroSeconds), time.toSeconds());
    assertEquals(timeUnit.toMinutes(baseTimeInMicroSeconds), time.toMinutes());
    assertEquals(timeUnit.toHours(baseTimeInMicroSeconds), time.toHours());
    assertEquals(timeUnit.toDays(baseTimeInMicroSeconds), time.toDays());
  }

}
