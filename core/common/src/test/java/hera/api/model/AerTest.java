/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static java.math.BigInteger.valueOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import hera.AbstractTestCase;
import hera.api.model.Aer.Unit;
import java.math.BigInteger;
import org.junit.Test;

public class AerTest extends AbstractTestCase {

  @Test
  public void testCreateWithUnit() {
    final Object[][] testParameters = new Object[][] {
        {"0", Unit.AER, valueOf(0L)},
        {"1", Unit.AER, valueOf(1L)},
        {"10", Unit.AER, valueOf(10L)},
        {"0.000000001", Unit.GAER, valueOf(1L)},
        {"0.01", Unit.GAER, valueOf(10000000L)},
        {"0", Unit.GAER, valueOf(0L)},
        {"1", Unit.GAER, valueOf(1000000000L)},
        {"10", Unit.GAER, valueOf(10000000000L)},
        {"10.1", Unit.GAER, valueOf(10100000000L)},
        {"0.000000000000000001", Unit.AERGO, valueOf(1L)},
        {"0.01", Unit.AERGO, new BigInteger("10000000000000000")},
        {"0", Unit.AERGO, new BigInteger("0")},
        {"1", Unit.AERGO, new BigInteger("1000000000000000000")},
        {"10", Unit.AERGO, new BigInteger("10000000000000000000")},
        {"10.1", Unit.AERGO, new BigInteger("10100000000000000000")}
    };

    for (final Object[] testParameter : testParameters) {
      final String amount = (String) testParameter[0];
      final Aer.Unit unit = (Aer.Unit) testParameter[1];
      final BigInteger expected = (BigInteger) testParameter[2];
      logger.debug("Amount: {}, Unit: {}, expected: {}", amount, unit, expected);
      final BigInteger aer = Aer.of(amount, unit).getValue();
      assertEquals(expected, aer);
    }
  }

  @Test
  public void testInvalidFormatAmount() {
    final Object[][] testParameters = new Object[][] {
        {"0.1", Unit.AER},
        {"-1", Unit.AER},
        {"-0.000000001", Unit.GAER},
        {"0.0000000001", Unit.GAER},
        {"0.0000000009", Unit.GAER},
        {"-0.000000000000000001", Unit.AERGO},
        {"0.0000000000000000001", Unit.AERGO},
        {"0.0000000000000000009", Unit.AERGO},
        {"1+0", Unit.AER},
        {"1+0", Unit.GAER},
        {"1+0", Unit.AERGO}
    };

    for (final Object[] testParameter : testParameters) {
      final String amount = (String) testParameter[0];
      final Unit unit = (Unit) testParameter[1];
      try {
        // when
        Aer.of(amount, unit);
        fail(String.format("Should throw exception on Amount: %s, Unit: %s", amount, unit));
      } catch (Exception e) {
        // then
      }
    }
  }

  @Test
  public void testAdd() {
    final Aer left = Aer.of("1", Unit.AER);
    final Aer right = Aer.of("2", Unit.AER);
    assertEquals(Aer.of("3", Unit.AER), left.add(right));
  }

  @Test
  public void shouldAddFailOnEmptyOperand() {
    final Object[][] parameters = new Object[][] {
        {Aer.EMPTY, Aer.ONE},
        {Aer.ONE, Aer.EMPTY}
    };

    for (final Object[] parameter : parameters) {
      final Aer left = (Aer) parameter[0];
      final Aer right = (Aer) parameter[1];
      try {
        // when
        left.add(right);
        fail();
      } catch (AssertionError e) {
        // then
      }
    }
  }

  @Test
  public void testSubstract() {
    final Aer left = Aer.of("1", Unit.AER);
    final Aer right = Aer.of("4", Unit.AER);
    assertEquals(Aer.ZERO, left.subtract(right));
    assertEquals(Aer.of("3", Unit.AER), right.subtract(left));
  }

  @Test
  public void shouldSubstractFailOnEmptyOperand() {
    final Object[][] parameters = new Object[][] {
        {Aer.EMPTY, Aer.ONE},
        {Aer.ONE, Aer.EMPTY}
    };

    for (final Object[] parameter : parameters) {
      final Aer left = (Aer) parameter[0];
      final Aer right = (Aer) parameter[1];
      try {
        // when
        left.subtract(right);
        fail();
      } catch (AssertionError e) {
        // then
      }
    }
  }

  @Test
  public void testCompareTo() {
    final Aer bottom = Aer.of("1", Unit.AER);
    final Aer middle = Aer.of("2", Unit.AER);
    final Aer top = Aer.of("3", Unit.AER);

    assertEquals(1, middle.compareTo(bottom));
    assertEquals(0, middle.compareTo(middle));
    assertEquals(-1, middle.compareTo(top));
  }

  @Test
  public void testCompareToOnNull() {
    try {
      final Aer aer = Aer.ZERO;
      aer.compareTo(null);
    } catch (AssertionError e) {
      // then
    }
  }

  @Test
  public void testToString() {
    final Object[][] testParameters = new Object[][] {
        {"1", Unit.AER, Unit.AER.getName() + "(value=1)"},
        {"1", Unit.GAER, Unit.GAER.getName() + "(value=0.000000001)"},
        {"1", Unit.AERGO, Unit.AERGO.getName() + "(value=0.000000000000000001)"},
    };

    for (final Object[] parameters : testParameters) {
      final String value = (String) parameters[0];
      final Unit unit = (Unit) parameters[1];
      final String expected = (String) parameters[2];
      final String actual = Aer.of(value).toString(unit);
      assertEquals(expected, actual);
    }
  }

}
