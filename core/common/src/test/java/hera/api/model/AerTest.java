/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static java.math.BigInteger.valueOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import hera.exception.InvalidAerFormatException;
import java.math.BigInteger;
import org.junit.Test;

public class AerTest {

  @Test
  public void testCreateWithAer() {
    final Object[][] testParameters = new Object[][] {
        {"0", valueOf(0L)},
        {"1", valueOf(1L)},
        {"0 aer", valueOf(0L)},
        {"1 aer", valueOf(1L)},
        {"1  aer", valueOf(1L)},
        {"10 aer", valueOf(10L)},
        {"10  aer", valueOf(10L)},
        {"1 AER", valueOf(1L)},
        {"1  AER", valueOf(1L)},
        {"10 AER", valueOf(10L)},
        {"10  AER", valueOf(10L)},
    };

    for (final Object[] testParameter : testParameters) {
      final String amount = (String) testParameter[0];
      final BigInteger expected = (BigInteger) testParameter[1];
      final BigInteger aer = Aer.of(amount).getValue();
      assertEquals(expected, aer);
    }
  }

  @Test
  public void testCreateWithGigaAer() {
    final Object[][] testParameters = new Object[][] {
        {"0.000000001 gaer", valueOf(1L)},
        {"0.01 gaer", valueOf(10_000_000L)},
        {"0 gaer", valueOf(0L)},
        {"1 gaer", valueOf(1_000_000_000L)},
        {"10 gaer", valueOf(10_000_000_000L)},
        {"10.1 gaer", valueOf(10_100_000_000L)},
        {"0.000000001 GAER", valueOf(1L)},
        {"0.01 GAER", valueOf(10_000_000L)},
        {"1 GAER", valueOf(1_000_000_000L)},
        {"10 GAER", valueOf(10_000_000_000L)},
        {"10.1 GAER", valueOf(10_100_000_000L)},
    };

    for (final Object[] testParameter : testParameters) {
      final String amount = (String) testParameter[0];
      final BigInteger expected = (BigInteger) testParameter[1];
      final BigInteger aer = Aer.of(amount).getValue();
      assertEquals(expected, aer);
    }
  }

  @Test
  public void testCreateWithAergo() {
    final Object[][] testParameters = new Object[][] {
        {"0.000000000000000001 aergo", valueOf(1L)},
        {"0.01 aergo", new BigInteger("10000000000000000")},
        {"0 aergo", new BigInteger("0")},
        {"1 aergo", new BigInteger("1000000000000000000")},
        {"10 aergo", new BigInteger("10000000000000000000")},
        {"10.1 aergo", new BigInteger("10100000000000000000")},
        {"0.000000000000000001 AERGO", valueOf(1L)},
        {"0.01 AERGO", new BigInteger("10000000000000000")},
        {"1 AERGO", new BigInteger("1000000000000000000")},
        {"10 AERGO", new BigInteger("10000000000000000000")},
        {"10.1 AERGO", new BigInteger("10100000000000000000")},
    };

    for (final Object[] testParameter : testParameters) {
      final String amount = (String) testParameter[0];
      final BigInteger expected = (BigInteger) testParameter[1];
      final BigInteger aer = Aer.of(amount).getValue();
      assertEquals(expected, aer);
    }
  }

  @Test
  public void testInvalidFormatAmount() {
    final Object[] testParameters = new Object[] {
        "0.1 aer",
        "-1 aer",
        "-0.000000001 gaer",
        "0.0000000001 gaer",
        "0.0000000009 gaer",
        "-0.000000000000000001 aergo",
        "0.0000000000000000001 aergo",
        "0.0000000000000000009 aergo",
        "1aer",
        "1gaer",
        "1aergo",
        "1+0 aer",
        "1j0 aer",
        "177a0 aer",
        "1 aerg",
        "1 gaerg",
        "1 ggaerg",
        "1 aergog",
        "1 gaergog",
        "1 gaeraergo"
    };

    for (final Object testParameter : testParameters) {
      final String invalidFormat = (String) testParameter;
      try {
        Aer.of(invalidFormat);
        fail("Should throw exception on \"" + invalidFormat + "\"");
      } catch (InvalidAerFormatException e) {
        // good we expected this
      }
    }
  }

  @Test
  public void testCompareTo() {
    final Aer bottom = Aer.of("1 aer");
    final Aer middle = Aer.of("2 aer");
    final Aer top = Aer.of("3 aer");

    assertEquals(1, middle.compareTo(bottom));
    assertEquals(0, middle.compareTo(middle));
    assertEquals(-1, middle.compareTo(top));
  }

  @Test(expected = NullPointerException.class)
  public void testCompareToOnNull() {
    final Aer aer = Aer.ZERO;
    aer.compareTo(null);
  }

}
