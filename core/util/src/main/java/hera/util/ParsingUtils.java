/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static hera.util.StringUtils.trim;
import static java.lang.Character.toLowerCase;
import static java.util.Arrays.asList;

import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;

public class ParsingUtils {

  /**
   * strings meaning boolean's true.
   */
  protected static final Collection<String> TRUES = new HashSet<>(asList("yes", "y", "true", "1"));

  /**
   * strings meaning boolean's false.
   */
  protected static final Collection<String> FALSES = new HashSet<>(asList("no", "n", "false", "0"));

  /**
   * Parsing Interval.
   * <p>
   * time units, which is based on milliseconds
   * </p>
   */
  public static final List<ScaleUnit> INTERVALS =
      Collections.unmodifiableList(asList(
          new ScaleUnit("microseconds", 1L),
          new ScaleUnit("µs", 1L),
          new ScaleUnit("milliseconds", 1_000L),
          new ScaleUnit("ms", 1_000L),
          new ScaleUnit("seconds", 1_000_000L),
          new ScaleUnit("sec", 1_000_000L),
          new ScaleUnit("minutes", 60_000_000L),
          new ScaleUnit("min", 60_000_000L),
          new ScaleUnit("m", 60_000_000L),
          new ScaleUnit("hours", 3_600_000_000L),
          new ScaleUnit("hr", 3_600_000_000L),
          new ScaleUnit("h", 3_600_000_000L),
          new ScaleUnit("s", 1_000_000L))
      );

  /**
   * Parse {@code str} and convert to {@code boolean}.
   * <p>
   *   Check {@code str} means {@code true} if {@code defaultValue} is {@code false}.
   *   Check {@code str} means {@code false} if {@code defaultValue} is {@code true}.
   * </p>
   *
   * @param str                 string to parse
   * @param defaultValueIsTrue  default value
   * @return boolean value from {@code str}
   *
   * @see ParsingUtils#TRUES
   * @see ParsingUtils#FALSES
   */
  public static boolean convertToBoolean(final String str, final boolean defaultValueIsTrue) {
    if (null == str) {
      return defaultValueIsTrue;
    }

    final String safeStr = trim(str).toLowerCase();

    if (defaultValueIsTrue) {
      return !FALSES.contains(safeStr);
    } else {
      return TRUES.contains(safeStr);
    }
  }

  /**
   * Parse {@code val} and convert to {@code boolean} type.
   *
   * @param val string to parse
   *
   * @return boolean value
   *
   * @see ParsingUtils#convertToBoolean(String, boolean)
   */
  public static boolean convertToBoolean(final String val) {
    return convertToBoolean(val, false);
  }

  /**
   * Parse {@code val} and convert to {@code int} type.
   *
   * @param val string to parse
   *
   * @return int value
   *
   * @see ParsingUtils#convertToInt(String, int)
   */
  public static int convertToInt(final String val) {
    return convertToInt(val, 0);
  }

  /**
   * Parse {@code val} and convert to {@code int} type.
   * <p>
   *   Return {@code defaultValue} if can't parse.
   * </p>
   *
   * @param val          string to parse
   * @param defaultValue default value
   *
   * @return int value
   *
   * @see NumberUtils#parse(String, Class)
   */
  public static int convertToInt(final String val, final int defaultValue) {
    try {
      return NumberUtils.parse(val, int.class);
    } catch (final Throwable e) {
      return defaultValue;
    }
  }

  /**
   * Parse {@code val} and convert to {@code long} type.
   *
   * @param val string to parse
   *
   * @return long value
   *
   * @see ParsingUtils#convertToLong(String)
   */
  public static long convertToLong(final String val) {
    return convertToLong(val, 0);
  }

  /**
   * Parse {@code val} and convert to {@code long} type.
   * <p>
   *   Return {@code defaultValue} if can't parse.
   * </p>
   *
   * @param val          string to parse
   * @param defaultValue default value
   *
   * @return long value
   *
   * @see NumberUtils#parse(String, Class)
   */
  public static long convertToLong(final String val, final int defaultValue) {
    try {
      return NumberUtils.parse(val, long.class);
    } catch (final Throwable e) {
      return defaultValue;
    }
  }

  /**
   * Parse {@code val} and convert to {@code long} type.
   * <p>
   *   Return default value if can't parse
   * </p>
   *
   * @param val          string to parse
   * @param defaultValue default value
   *
   * @return long value
   *
   * @see NumberUtils#parse(String, Class)
   */
  public static long convertToLong(final String val, final long defaultValue) {
    if (null == val) {
      return defaultValue;
    }
    try {
      return NumberUtils.parse(val, long.class);
    } catch (final Throwable e) {
      return defaultValue;
    }
  }

  /**
   * Parse {@code val} and convert to {@code double} type.
   *
   * @param val string to parse
   *
   * @return double value
   *
   * @see ParsingUtils#convertToDouble(String, double)
   */
  public static double convertToDouble(final String val) {
    return convertToDouble(val, 0);
  }

  /**
   * Parse {@code val} and convert to {@code double} type.
   * <p>
   *   Return {@code defaultValue} if can't parse.
   * </p>
   *
   * @param val          string to parse
   * @param defaultValue default value
   *
   * @return double value
   *
   * @see NumberUtils#parse(String, Class)
   */
  public static double convertToDouble(final String val, final double defaultValue) {
    if (null == val) {
      return defaultValue;
    }
    try {
      return NumberUtils.parse(val, double.class);
    } catch (final Throwable e) {
      return defaultValue;
    }
  }

  /**
   * Parse {@code str} and convert capacity in bytes.
   * <p>
   *   Return {@code defaultValue} if can't parse
   * </p>
   *
   * @param str          string to parse
   * @param defaultValue default value
   *
   * @return parsed value
   */
  public static long convertToCapacity(
      final String str,
      final long defaultValue) {
    if (null == str) {
      return defaultValue;
    }
    String safe = str.trim();
    char unit = safe.charAt(safe.length() - 1);
    String valuePart = safe.substring(0, safe.length() - 1);
    long value = Long.parseLong(valuePart);
    if ('k' == toLowerCase(unit)) {
      return 1000 * value;
    } else if ('m' == toLowerCase(unit)) {
      return 1000000 * value;
    } else if ('g' == toLowerCase(unit)) {
      return 1000000000 * value;
    }
    return value;
  }

  /**
   * Parse {@code val} and convert time in microseconds.
   *
   * @param val string to parse
   *
   * @return time in microseconds
   *
   * @throws ParseException Fail to parse
   */
  public static long convertToTime(final String val) throws ParseException {
    if (null == val) {
      return 0;
    }

    final String lowerVal = val.toLowerCase();
    for (final ScaleUnit unit : INTERVALS) {
      final String units = unit.units;
      if (lowerVal.endsWith(units)) {
        final String digits = lowerVal.substring(0, lowerVal.length() - units.length()).trim();
        final Double microSeconds = Double.parseDouble(digits) * unit.scale;
        return microSeconds.longValue();
      }
    }

    throw new ParseException(val, 0);
  }

  /**
   * Parse {@code val} and convert time in {@code unit}.
   *
   * @param val  string to parse
   * @param unit time unit to convert
   *
   * @return time in {@code unit}
   *
   * @throws ParseException Fail to parse
   *
   * @see ParsingUtils#convertToTime(String)
   */
  public static long parse(
      final String val,
      final TimeUnit unit)
      throws ParseException {
    long time = convertToTime(val);
    return unit.convert(time, TimeUnit.MICROSECONDS);
  }

  /**
   * ScaleUnit.
   */
  @RequiredArgsConstructor
  public static class ScaleUnit {

    /**
     * time unit.
     */
    public final String units;

    /**
     * Scale for a standard unit.
     */
    public final long scale;
  }

}

