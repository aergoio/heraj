/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;

import hera.exception.HerajException;
import hera.exception.InvalidAerFormatException;
import java.math.BigDecimal;
import java.math.BigInteger;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class Aer implements Comparable<Aer> {

  public static final Aer ZERO = new Aer(BigInteger.valueOf(0L));

  public static final Aer ONE = new Aer(BigInteger.valueOf(1L));

  public static final String AER = "aer";

  public static final String GAER = "gaer";

  public static final String AERGO = "aergo";

  public static final BigDecimal MINIMUM_AER = new BigDecimal("1");

  public static final BigDecimal MINIMUM_GAER = new BigDecimal("1.E-9");

  public static final BigDecimal MINIMUM_AERGO = new BigDecimal("1.E-18");

  public static final BigDecimal AER_TIMES_AER = new BigDecimal("1");

  public static final BigDecimal GAER_TIMES_AER = new BigDecimal("1.E9");

  public static final BigDecimal AERGO_TIMES_AER = new BigDecimal("1.E18");

  /**
   * Create {@code Aergo} instance. {@code amount} must be in form <b>${AMOUNT} ${UNIT}</b>.
   * Following format is true.
   *
   * <pre>
   * <code>
   *   1 // consided as 1 aer
   *   1 aer
   *   1 AER
   *   1 gaer
   *   1 GAER
   *   0.01 gaer
   *   0.000000001 gaer // 1 aer
   *   1 aergo
   *   1 AERGO
   *   0.01 aergo
   *   0.000000000000000001 aergo // 1 aer
   * </code>
   * </pre>
   *
   * <p>
   * Since <b>aer</b> is an atomic unit, following format is invalid.
   * </p>
   *
   * <pre>
   * <code>
   *   0.1 aer
   *   0.0000000001 gaer
   *   0.0000000000000000001 aergo
   * </code>
   * </pre>
   *
   * @param amount an amount in string
   * @return an aergo instance
   * @throws InvalidAerFormatException if invalid amount format
   */
  public static Aer of(final String amount) {
    return new Aer(amount);
  }

  /**
   * Create an {@code Aer} instance.
   *
   * @param amount an amount in <b>aer</b>
   * @return an aergo instance
   */
  public static Aer of(final BigInteger amount) {
    return new Aer(amount);
  }

  @Getter
  protected final BigInteger value;

  /**
   * Create {@code Aergo} instance. {@code amount} must be in form <b>${AMOUNT} ${UNIT}</b>.
   * Following format is true.
   *
   * <pre>
   * <code>
   *   1 // consided as 1 aer
   *   1 aer
   *   1 AER
   *   1 gaer
   *   1 GAER
   *   0.01 gaer
   *   0.000000001 gaer // 1 aer
   *   1 aergo
   *   1 AERGO
   *   0.01 aergo
   *   0.000000000000000001 aergo // 1 aer
   * </code>
   * </pre>
   *
   * <p>
   * Since <b>aer</b> is an atomic unit, following format is invalid.
   * </p>
   *
   * <pre>
   * <code>
   *   0.1 aer
   *   0.0000000001 gaer
   *   0.0000000000000000001 aergo
   * </code>
   * </pre>
   *
   * @param amount an amount in string
   * @throws InvalidAerFormatException if invalid amount format
   */
  public Aer(final String amount) {
    assertNotNull(amount, new HerajException("Amount must not null"));
    this.value = parse(amount);
  }

  /**
   * Aer constructor.
   *
   * @param amount an amount in <b>aer</b>
   */
  public Aer(final BigInteger amount) {
    this.value =
        null != amount ? (amount.compareTo(BigInteger.ZERO) >= 0 ? amount : BigInteger.ZERO)
            : BigInteger.ZERO;
  }

  protected BigInteger parse(final String amount) {
    final String trimmedAmount = amount.toLowerCase().trim();

    final String[] splited = trimmedAmount.split("\\s+");
    if (splited.length == 1) {
      return parseValueAndUnit(splited[0], AER);
    } else if (splited.length == 2) {
      return parseValueAndUnit(splited[0], splited[1]);
    } else {
      throw new InvalidAerFormatException(trimmedAmount + " is unacceptable");
    }
  }

  protected BigInteger parseValueAndUnit(final String value, final String unit) {
    BigDecimal minimum = null;
    BigDecimal timesToAer = null;

    if (AER.equals(unit)) {
      minimum = MINIMUM_AER;
      timesToAer = AER_TIMES_AER;
    } else if (GAER.equals(unit)) {
      minimum = MINIMUM_GAER;
      timesToAer = GAER_TIMES_AER;
    } else if (AERGO.equals(unit)) {
      minimum = MINIMUM_AERGO;
      timesToAer = AERGO_TIMES_AER;
    } else {
      throw new InvalidAerFormatException(
          String.format("Invalid unit format %s (allowed : %s, %s, %s)", unit, AER, GAER, AERGO));
    }

    BigDecimal parsedValue;
    try {
      parsedValue = new BigDecimal(value);
    } catch (NumberFormatException e) {
      throw new InvalidAerFormatException(e);
    }

    if (parsedValue.compareTo(BigDecimal.ZERO) == -1) {
      throw new InvalidAerFormatException("Aer should be postive");
    }

    if (parsedValue.compareTo(BigDecimal.ZERO) > 0 && parsedValue.compareTo(minimum) == -1) {
      throw new InvalidAerFormatException(
          String.format("Aer is smaller then minimum : %s %s", minimum.toPlainString(), unit));
    }

    return parsedValue.multiply(timesToAer).toBigInteger();
  }

  /**
   * {@inheritDoc}
   *
   * @throws NullPointerException if the specified object is null
   */
  @Override
  public int compareTo(final Aer other) {
    return this.value.compareTo(other.value);
  }

}
