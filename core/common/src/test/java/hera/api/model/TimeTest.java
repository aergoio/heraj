/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static org.junit.Assert.assertEquals;

import hera.api.model.internal.Time;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

public class TimeTest {

  @Test
  public void testConversion() {
    final long baseTimeInMicroSeconds = 100000000000000L;
    final TimeUnit timeUnit = TimeUnit.MILLISECONDS;
    final Time time = Time.of(baseTimeInMicroSeconds);
    assertEquals(timeUnit.toNanos(baseTimeInMicroSeconds), time.toNanoseconds());
    assertEquals(timeUnit.toMicros(baseTimeInMicroSeconds), time.toMicroSeconds());
    assertEquals(timeUnit.toMillis(baseTimeInMicroSeconds), time.toMilliseconds());
    assertEquals(timeUnit.toSeconds(baseTimeInMicroSeconds), time.toSeconds());
    assertEquals(timeUnit.toMinutes(baseTimeInMicroSeconds), time.toMinutes());
    assertEquals(timeUnit.toHours(baseTimeInMicroSeconds), time.toHours());
    assertEquals(timeUnit.toDays(baseTimeInMicroSeconds), time.toDays());
  }

}
