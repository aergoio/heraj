/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import org.junit.Test;

public class NumberUtilsTest {

  @Test
  public void testConvert() throws ClassNotFoundException {
    final Object[][] testParameters = new Object[][] {{127, Byte.class, (byte) 127},
        {128, Short.class, (short) 128}, {255, Integer.class, 255}, {256L, Long.class, 256L},
        {new BigInteger("511"), BigInteger.class, BigInteger.valueOf(511)},
        {new BigDecimal("512"), BigInteger.class, new BigDecimal("512").toBigInteger()},
        {1023f, Float.class, 1023f}, {1024d, Double.class, 1024d},
        {new BigDecimal("2047"), BigDecimal.class, new BigDecimal("2047")}};

    for (final Object[] testParameter : testParameters) {
      Object expected = Class.forName(testParameter[2].getClass().getName()).cast(testParameter[2]);
      Number number = (Number) testParameter[0];
      Class<?> targetClass = (Class<?>) testParameter[1];
      assertEquals(expected, NumberUtils.convert(number, targetClass));
    }
  }

  @Test
  public void testThrowOverflowException() {
    final Object[][] testParameters =
        new Object[][] {{Byte.MIN_VALUE - 1, Byte.class}, {Byte.MAX_VALUE + 1, Byte.class},
            {Short.MIN_VALUE - 1, Short.class}, {Short.MAX_VALUE + 1, Short.class},
            {-2147483649L, Integer.class}, {2147483648L, Integer.class}};

    for (final Object[] testParameter : testParameters) {
      Number number = (Number) testParameter[0];
      Class<?> targetClass = (Class<?>) testParameter[1];
      try {
        NumberUtils.convert(number, targetClass);
      } catch (Exception e) {
        assertSame(IllegalArgumentException.class, e.getClass());
      }
    }
  }

  @Test
  public void testParse() throws ClassNotFoundException {
    final Object[][] testParameters = new Object[][] {{"127", Byte.class, (byte) 127},
        {"128", Short.class, (short) 128}, {"255", Integer.class, 255}, {"256", Long.class, 256L},
        {"511", BigInteger.class, BigInteger.valueOf(511)},
        {"512", BigInteger.class, new BigDecimal("512").toBigInteger()},
        {"1023", Float.class, 1023f}, {"1024", Double.class, 1024d},
        {"2047", BigDecimal.class, new BigDecimal("2047")}};

    for (final Object[] testParameter : testParameters) {
      Object expected = Class.forName(testParameter[2].getClass().getName()).cast(testParameter[2]);
      String text = (String) testParameter[0];
      Class<?> clazz = (Class<?>) testParameter[1];
      assertEquals(expected, NumberUtils.parse(text, clazz));
    }
  }

  @Test
  public void testParseOnNumberFormat() throws ClassNotFoundException {
    final Object[][] testParameters = new Object[][] {
        {"127", Byte.class, NumberFormat.getInstance(), (byte) 127},
        {"1,280", Short.class, NumberFormat.getInstance(), (short) 1280},
        {"25,500", Integer.class, NumberFormat.getInstance(), 25500},
        {"256,000", Long.class, NumberFormat.getInstance(), 256000L},
        {"5,110,000", BigInteger.class, new DecimalFormat(), BigInteger.valueOf(5110000)},
        {"51,200,000", BigInteger.class, new DecimalFormat(),
            new BigDecimal("51200000").toBigInteger()},
        {"10,230,000.1023", Float.class, new DecimalFormat(), 10230000.1023f},
        {"10,240,000.1024", Double.class, new DecimalFormat(), 10240000.1024d}, {"204,700,000.2047",
            BigDecimal.class, new DecimalFormat(), new BigDecimal("204700000.2047")}};

    assertNotNull(NumberUtils.parse("127", Byte.class, null));

    for (final Object[] testParameter : testParameters) {
      Object expected = Class.forName(testParameter[3].getClass().getName()).cast(testParameter[3]);
      String text = (String) testParameter[0];
      Class<?> targetClass = (Class<?>) testParameter[1];
      NumberFormat numberFormat = (NumberFormat) testParameter[2];
      assertEquals(expected, NumberUtils.parse(text, targetClass, numberFormat));
    }
  }

  @Test
  public void testDecodeBigInteger()
      throws ClassNotFoundException, NoSuchMethodException, SecurityException,
      IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    final Object[][] testParameters =
        new Object[][] {{"012", new BigInteger("10")},
            {"-014", new BigInteger("-12")},
            {"16", new BigInteger("16")},
            {"-16", new BigInteger("-16")},
            {"0x18", new BigInteger("24")},
            {"-0X20", new BigInteger("-32")},
            {"#22", new BigInteger("34")},
            {"-#24", new BigInteger("-36")}};

    for (final Object[] testParameter : testParameters) {
      BigInteger expected = (BigInteger) testParameter[1];
      String value = (String) testParameter[0];
      Method method = NumberUtils.class.getDeclaredMethod("decodeBigInteger", String.class);
      method.setAccessible(true);
      assertEquals(expected, method.invoke(NumberUtils.class, value));
    }

  }
}
