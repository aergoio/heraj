/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

public class ParsingUtilsTest {

  @Test
  public void testConvertToBooleanOnDefaultValue() {
    final Object[][] testParameters = new Object[][] {{"no", false, false}, {"n", false, false},
        {"false", false, false}, {"0", false, false}, {"No", false, false}, {"False", false, false},
        {"no", true, false}, {"n", true, false}, {"false", true, false}, {"0", true, false},
        {"No", true, false}, {"False", true, false}, {"yes", false, true}, {"y", false, true},
        {"true", false, true}, {"1", false, true}, {"Yes", false, true}, {"True", false, true},
        {"yes", true, true}, {"y", true, true}, {"true", true, true}, {"1", true, true},
        {"Yes", true, true}, {"True", true, true}};

    assertTrue(ParsingUtils.convertToBoolean(null, true));
    assertFalse(ParsingUtils.convertToBoolean(null, false));

    for (final Object[] testParameter : testParameters) {
      boolean expected = (Boolean) testParameter[2];
      String str = (String) testParameter[0];
      boolean defaultValue = (Boolean) testParameter[1];
      assertEquals(expected, ParsingUtils.convertToBoolean(str, defaultValue));
    }
  }

  @Test
  public void testConvertToBoolean() {
    final Object[][] testParameters = new Object[][] {{"no", false}, {"n", false}, {"false", false},
        {"0", false}, {"No", false}, {"False", false}, {"yes", true}, {"y", true}, {"true", true},
        {"1", true}, {"Yes", true}, {"True", true}};

    for (final Object[] testParameter : testParameters) {
      boolean expected = (Boolean) testParameter[1];
      String val = (String) testParameter[0];
      assertEquals(expected, ParsingUtils.convertToBoolean(val));
    }
  }

  @Test
  public void testConvertToInt() {
    final Object[][] testParameters =
        new Object[][] {{"1", 1}, {"10", 10}, {"100", 100}, {"Not", 0}};

    for (final Object[] testParameter : testParameters) {
      int expected = (Integer) testParameter[1];
      String val = (String) testParameter[0];
      assertEquals(expected, ParsingUtils.convertToInt(val));
    }
  }

  @Test
  public void testConvertToIntOnDefaultValue() {
    final Object[][] testParameters = new Object[][] {{"1", 0, 1}, {"10", 1, 10}, {"100", 2, 100},
        {"Not01", 0, 0}, {"Not02", 1, 1}, {"Not03", 2, 2}};

    assertEquals(0, ParsingUtils.convertToInt(null, 0));
    assertEquals(1, ParsingUtils.convertToInt(null, 1));

    for (final Object[] testParameter : testParameters) {
      int expected = (Integer) testParameter[2];
      String val = (String) testParameter[0];
      int defaultValue = (Integer) testParameter[1];
      assertEquals(expected, ParsingUtils.convertToInt(val, defaultValue));
    }
  }

  @Test
  public void testConvertToLong() {
    final Object[][] testParameters =
        new Object[][] {{"1", 1L}, {"10", 10L}, {"100", 100L}, {"Not", 0L}};

    for (final Object[] testParameter : testParameters) {
      long expected = (Long) testParameter[1];
      String val = (String) testParameter[0];
      assertEquals(expected, ParsingUtils.convertToLong(val));
    }
  }

  @Test
  public void testConvertToLongOnDefaultValue() {
    final Object[][] testParameters = new Object[][] {{"1", 0L, 1L}, {"10", 1L, 10L},
        {"100", 2L, 100L}, {"Not01", 0L, 0L}, {"Not02", 1L, 1L}, {"Not03", 2L, 2L}};

    assertEquals(0, ParsingUtils.convertToLong(null, 0));
    assertEquals(1, ParsingUtils.convertToLong(null, 1));

    for (final Object[] testParameter : testParameters) {
      long expected = (Long) testParameter[2];
      String val = (String) testParameter[0];
      long defaultValue = (Long) testParameter[1];
      assertEquals(expected, ParsingUtils.convertToLong(val, defaultValue));
    }
  }

  @Test
  public void testConvertToDouble() {
    final Object[][] testParameters =
        new Object[][] {{"1.1", 1.1}, {"10.21", 10.21}, {"100.321", 100.321}, {"Not", 0.0}};

    for (final Object[] testParameter : testParameters) {
      double expected = (Double) testParameter[1];
      String val = (String) testParameter[0];
      assertEquals(expected, ParsingUtils.convertToDouble(val), 1e-15);
    }
  }

  @Test
  public void testConvertToDoubleOnDefaultValue() {
    final Object[][] testParameters = new Object[][] {{"1.1", 0.0, 1.1}, {"10.21", 1.0, 10.21},
        {"100.321", 2.0, 100.321}, {"Not01", 0.0, 0.0}, {"Not02", 1.0, 1.0}, {"Not03", 2.0, 2.0}};

    assertEquals(0.0, ParsingUtils.convertToDouble(null, 0.0), 1e-15);
    assertEquals(1.0, ParsingUtils.convertToDouble(null, 1.0), 1e-15);

    for (final Object[] testParameter : testParameters) {
      double expected = (Double) testParameter[2];
      String val = (String) testParameter[0];
      double defaultValue = (Double) testParameter[1];
      assertEquals(expected, ParsingUtils.convertToDouble(val, defaultValue), 1e-15);
    }
  }

  @Test
  public void testConvertToCapacityOnDefaultValue() {
    final Object[][] testParameters =
        new Object[][] {{"1k", 0L, 1000L}, {"1m", 1L, 1000000L}, {"1g", 2L, 1000000000L},
            {"12K", 0L, 12000L}, {"34M", 1L, 34000000L}, {"56G", 2L, 56000000000L}};

    assertEquals(0L, ParsingUtils.convertToCapacity(null, 0L));
    assertEquals(1L, ParsingUtils.convertToCapacity(null, 1L));

    for (final Object[] testParameter : testParameters) {
      long expected = (Long) testParameter[2];
      String str = (String) testParameter[0];
      long defaultValue = (Long) testParameter[1];

      assertEquals(expected, ParsingUtils.convertToCapacity(str, defaultValue));
    }
  }

  @Test
  public void testConvertToTime() throws ParseException {
    final Object[][] testParameters = new Object[][] {{"1µs", 1L}, {"1.1µs", 1L},
        {"1microseconds", 1L}, {"1ms", 1000L}, {"1.1ms", 1100L}, {"1milliseconds", 1000L},
        {"1.1s", 1100000L}, {"1s", 1000000L}, {"1sec", 1000000L}, {"1seconds", 1000000L},
        {"1m", 60000000L}, {"1.1m", 66000000L}, {"1min", 60000000L},
        {"1minutes", 60000000L}, {"1h", 3600000000L}, {"1.1h", 3960000000L},
        {"1hr", 3600000000L}, {"1hours", 3600000000L}};

    try {
      ParsingUtils.convertToTime("Not");
    } catch (ParseException e) {
      assertEquals("Not", e.getMessage());
      assertEquals(0, e.getErrorOffset());
    }

    for (final Object[] testParameter : testParameters) {
      long expected = (Long) testParameter[1];
      String val = (String) testParameter[0];
      assertEquals(expected, ParsingUtils.convertToTime(val));
    }
  }

  @Test
  public void testParse() throws ParseException {
    final Object[][] testParameters = new Object[][] {{"1ms", TimeUnit.NANOSECONDS, 1000000L},
        {"1ms", TimeUnit.MICROSECONDS, 1000L}, {"1ms", TimeUnit.MILLISECONDS, 1L},
        {"1ms", TimeUnit.SECONDS, 0L}, {"1ms", TimeUnit.MINUTES, 0L},
        {"1ms", TimeUnit.HOURS, 0L}, {"1ms", TimeUnit.DAYS, 0L},
        {"1s", TimeUnit.NANOSECONDS, 1000000000L}, {"1s", TimeUnit.MICROSECONDS, 1000000L},
        {"1s", TimeUnit.MILLISECONDS, 1000L}, {"1s", TimeUnit.SECONDS, 1L},
        {"1s", TimeUnit.MINUTES, 0L}, {"1s", TimeUnit.HOURS, 0L},
        {"1s", TimeUnit.DAYS, 0L}, {"1m", TimeUnit.NANOSECONDS, 60000000000L},
        {"1m", TimeUnit.MICROSECONDS, 60000000L}, {"1m", TimeUnit.MILLISECONDS, 60000L},
        {"1m", TimeUnit.SECONDS, 60L}, {"1m", TimeUnit.MINUTES, 1L},
        {"1m", TimeUnit.HOURS, 0L}, {"1m", TimeUnit.DAYS, 0L}};

    for (final Object[] testParameter : testParameters) {
      long expected = (Long) testParameter[2];
      String val = (String) testParameter[0];
      TimeUnit unit = (TimeUnit) testParameter[1];
      assertEquals(expected, ParsingUtils.parse(val, unit));
    }
  }

  @Test
  public void shouldBeFalseOnNull() {
    assertFalse(ParsingUtils.convertToBoolean(null));
  }

  @Test
  public void shouldBeZeroOnNull() throws ParseException {
    assertEquals(0, ParsingUtils.convertToInt(null));
    assertEquals(0, ParsingUtils.convertToLong(null));
    assertEquals(0, 0, ParsingUtils.convertToDouble(null));
    assertEquals(0, ParsingUtils.convertToTime(null));
  }
}
