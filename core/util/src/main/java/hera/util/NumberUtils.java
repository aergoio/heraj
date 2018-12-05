/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static hera.util.ValidationUtils.assertNotNull;
import static hera.util.ValidationUtils.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;

public class NumberUtils {

  /**
   * Convert {@code number} to {@code targetClass} type.
   *
   * @param <T> type to convert
   * @param number instance to convert
   * @param targetClass type class to convert
   * @return converted instance
   * @throws IllegalArgumentException No rule for conversion
   */
  @SuppressWarnings("unchecked")
  public static <T> T convert(final Number number, final Class<T> targetClass)
      throws IllegalArgumentException {

    assertNotNull(number, "Number must not be null");
    assertNotNull(targetClass, "Target class must not be null");

    if (targetClass.isInstance(number)) {
      return (T) number;
    } else if (targetClass.equals(Byte.class) || targetClass.equals(byte.class)) {
      final long value = number.longValue();
      if (value < Byte.MIN_VALUE || Byte.MAX_VALUE < value) {
        throwOverflowException(number, targetClass);
      }
      return (T) Byte.valueOf((byte) value);
    } else if (targetClass.equals(Short.class) || targetClass.equals(short.class)) {
      final long value = number.longValue();
      if (value < Short.MIN_VALUE || Short.MAX_VALUE < value) {
        throwOverflowException(number, targetClass);
      }
      return (T) Short.valueOf((short) value);
    } else if (targetClass.equals(Integer.class) || targetClass.equals(int.class)) {
      final long value = number.longValue();
      if (value < Integer.MIN_VALUE || Integer.MAX_VALUE < value) {
        throwOverflowException(number, targetClass);
      }
      return (T) Integer.valueOf((int) value);
    } else if (targetClass.equals(Long.class) || targetClass.equals(long.class)) {
      return (T) Long.valueOf(number.longValue());
    } else if (targetClass.equals(BigInteger.class)) {
      if (number instanceof BigDecimal) {
        // do not lose precision - use BigDecimal's own conversion
        return (T) ((BigDecimal) number).toBigInteger();
      } else {
        // original value is not a Big* number - use standard long conversion
        return (T) BigInteger.valueOf(number.longValue());
      }
    } else if (targetClass.equals(Float.class) || targetClass.equals(float.class)) {
      return (T) Float.valueOf(number.floatValue());
    } else if (targetClass.equals(Double.class) || targetClass.equals(double.class)) {
      return (T) Double.valueOf(number.doubleValue());
    } else if (targetClass.equals(BigDecimal.class)) {
      // always use BigDecimal(String) here to avoid unpredictability of BigDecimal(double)
      // (see BigDecimal javadoc for details)
      return (T) new BigDecimal(number.toString());
    }
    throw new IllegalArgumentException(
        "Could not convert number [" + number + "] of type [" + number.getClass().getName()
            + "] to unknown target class [" + targetClass.getName() + "]");
  }

  /**
   * Throw {@link IllegalArgumentException} when {@code number}'s value is out of bound
   * {@code targetClass}'s range.
   *
   * @param number value instance
   * @param targetClass container type
   */
  private static void throwOverflowException(final Number number, final Class<?> targetClass) {
    throw new IllegalArgumentException(
        "Could not convert number [" + number + "] of type [" + number.getClass().getName()
            + "] to target class [" + targetClass.getName() + "]: overflow");
  }

  /**
   * Parse {@code text} and convert number.
   *
   * @param <T> type to convert
   * @param text string to parse
   * @param clazz type class to convert
   *
   * @return converted instance
   */
  @SuppressWarnings("unchecked")
  public static <T> T parse(final String text, final Class<?> clazz) {
    assertNotNull(text, "Text must not be null");
    assertNotNull(clazz, "Target class must not be null");

    final String trimmed = StringUtils.trim(text);

    if (clazz.equals(Byte.class) || clazz.equals(byte.class)) {
      return (T) Byte.decode(trimmed);
    } else if (clazz.equals(Short.class) || clazz.equals(short.class)) {
      return (T) Short.decode(trimmed);
    } else if (clazz.equals(Integer.class) || clazz.equals(int.class)) {
      return (T) Integer.decode(trimmed);
    } else if (clazz.equals(Long.class) || clazz.equals(long.class)) {
      return (T) Long.decode(trimmed);
    } else if (clazz.equals(BigInteger.class)) {
      return (T) decodeBigInteger(trimmed);
    } else if (clazz.equals(Float.class) || clazz.equals(float.class)) {
      return (T) Float.valueOf(trimmed);
    } else if (clazz.equals(Double.class) || clazz.equals(double.class)) {
      return (T) Double.valueOf(trimmed);
    } else if (clazz.equals(BigDecimal.class) || clazz.equals(Number.class)) {
      return (T) new BigDecimal(trimmed);
    } else {
      throw new IllegalArgumentException(
          "Cannot convert String [" + text + "] to target class [" + clazz.getName() + "]");
    }
  }

  /**
   * Parse string {@code text} as {@code numberFormat} and convert to {@code targetClass} type
   * instance.
   *
   * @param <T> type to convert
   * @param text string to parse
   * @param targetClass type class to convert
   * @param numberFormat format to parse
   * @return converted number
   */
  public static <T> T parse(final String text, final Class<T> targetClass,
      final NumberFormat numberFormat) {
    final boolean noFormat = null == numberFormat;
    if (noFormat) {
      return parse(text, targetClass);
    }

    assertNotNull(text, "Text must not be null");
    assertNotNull(targetClass, "Target class must not be null");
    DecimalFormat decimalFormat = null;
    boolean resetBigDecimal = false;
    if (numberFormat instanceof DecimalFormat) {
      decimalFormat = (DecimalFormat) numberFormat;
      if (BigDecimal.class.equals(targetClass) && !decimalFormat.isParseBigDecimal()) {
        decimalFormat.setParseBigDecimal(true);
        resetBigDecimal = true;
      }
    }

    try {
      final Number number = numberFormat.parse(StringUtils.trim(text));
      return convert(number, targetClass);
    } catch (final ParseException ex) {
      throw new IllegalArgumentException("Could not parse number: " + ex.getMessage(), ex);
    } finally {
      if (resetBigDecimal) {
        decimalFormat.setParseBigDecimal(false);
      }
    }
  }

  /**
   * Parse {@code value} and convert to {@link BigInteger}.
   * <p>
   * Parse as hexa decimal if it has '0x' prefix. Parse as octal decimal if it has '0' prefix.
   * </p>
   *
   * @param value string to convert
   *
   * @return converted {@link BigInteger} instance
   */
  private static BigInteger decodeBigInteger(final String value) {
    int radix = 10;
    int index = 0;
    boolean negative = false;

    // Handle minus sign, if present.
    if (value.startsWith("-")) {
      negative = true;
      ++index;
    }

    // Handle radix specifier, if present.
    if (value.startsWith("0x", index) || value.startsWith("0X", index)) {
      index += 2;
      radix = 16;
    } else if (value.startsWith("#", index)) {
      ++index;
      radix = 16;
    } else if (value.startsWith("0", index) && 1 + index < value.length()) {
      ++index;
      radix = 8;
    }

    final BigInteger result = new BigInteger(value.substring(index), radix);
    return (negative ? result.negate() : result);
  }

  /**
   * Convert {@link BigInteger} into a byte array without additional sign byte to represent
   * canonical two's-complement form. Eg. 255 is {@code "11111111"} not
   * {@code "00000000 1111 1111"}.
   *
   * @param postiveNumber a positive bigInteger
   * @return a converted byte array
   */
  public static byte[] postiveToByteArray(final BigInteger postiveNumber) {
    assertNotNull(postiveNumber, "Argument must not be null");
    assertTrue(postiveNumber.compareTo(BigInteger.ZERO) >= 0,
        "Argument must greater than or equals to 0");

    final byte[] raw = postiveNumber.toByteArray();
    final int postiveByteCapacity =
        postiveNumber.equals(BigInteger.ZERO) ? 1 : (postiveNumber.bitLength() + 7) >>> 3;
    if (raw.length > postiveByteCapacity) {
      return Arrays.copyOfRange(raw, 1, raw.length);
    }
    return raw;
  }

  /**
   * Convert rawBytes to bigInteger. A rawBytes is considered only as positive (no extra sign bit).
   *
   * @param rawBytes a rawBytes representing positive number without sign bit.
   * @return a converted {@code BigInteger}
   */
  public static BigInteger byteArrayToPostive(final byte[] rawBytes) {
    assertNotNull(rawBytes, "Argument must not be null");

    if (rawBytes.length == 0) {
      return BigInteger.ZERO;
    }

    byte[] canonicalBytes = rawBytes;
    if ((rawBytes[0] & 0x80) != 0) {
      canonicalBytes = new byte[rawBytes.length + 1];
      canonicalBytes[0] = 0x00;
      System.arraycopy(rawBytes, 0, canonicalBytes, 1, rawBytes.length);
    }
    return new BigInteger(canonicalBytes);
  }

}
