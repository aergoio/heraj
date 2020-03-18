/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotEquals;
import static hera.util.ValidationUtils.assertNotNull;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.exception.HerajException;
import java.math.BigDecimal;
import java.math.BigInteger;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@ApiAudience.Public
@ApiStability.Unstable
@EqualsAndHashCode
public class Aer implements Comparable<Aer> {

  public static final String EMPTY_STRING = "Aer(EMPTY)";

  public static final Aer EMPTY = new Aer();

  public static final Aer ZERO = new Aer(BigInteger.ZERO);

  public static final Aer ONE = new Aer(BigInteger.ONE);

  public static final Aer GIGA_ONE = new Aer("1", Unit.GAER);

  public static final Aer AERGO_ONE = new Aer("1", Unit.AERGO);

  /**
   * Create {@code Aer} instance.
   *
   * @param amount an amount in string which is considered as {@link Unit#AER}
   * @return an aergo instance
   */
  public static Aer of(final String amount) {
    return new Aer(amount);
  }

  /**
   * Create {@code Aer} instance.
   *
   * @param amount an amount in string
   * @param unit   an unit {@link Unit}
   * @return an aergo instance
   */
  public static Aer of(final String amount, final Unit unit) {
    return new Aer(amount, unit);
  }

  /**
   * Create an {@code Aer} instance.
   *
   * @param amount an amount in {@link Unit#AER}
   * @return an aergo instance
   */
  public static Aer of(final BigInteger amount) {
    return new Aer(amount);
  }

  /**
   * An unit to represent aergo. Consist of {@code AER, GAER, AERGO}.
   */
  @RequiredArgsConstructor
  public enum Unit {
    AER("Aer", new BigDecimal("1"), new BigDecimal("1")),
    GAER("Gaer", new BigDecimal("1.E-9"), new BigDecimal("1.E9")),
    AERGO("Aergo", new BigDecimal("1.E-18"), new BigDecimal("1.E18"));

    @Getter
    protected final String name;

    @Getter
    protected final BigDecimal minimum;

    @Getter
    protected final BigDecimal ratio;
  }

  @Getter
  protected final BigInteger value;

  /**
   * Create {@code Aer} instance.
   *
   * @param amount an amount in string which is considered as {@link Unit#AER}
   */
  public Aer(final String amount) {
    this(amount, Unit.AER);
  }

  /**
   * Create {@code Aer} instance.
   *
   * @param amount an amount in string
   * @param unit   an unit {@link Unit}
   */
  public Aer(final String amount, final Unit unit) {
    assertNotNull(amount, "Amount must not null");
    assertNotNull(unit, "Unit must not null");
    this.value = parse(amount, unit);
  }

  /**
   * Create an {@code Aer} instance.
   *
   * @param amount an amount in {@link Unit#AER}
   */
  public Aer(final BigInteger amount) {
    assertNotNull(amount, "Amount must not null");
    if (-1 == amount.compareTo(BigInteger.ZERO)) {
      throw new HerajException("Amount must be positive");
    }
    this.value = amount;
  }

  protected Aer() {
    this.value = null;
  }

  protected BigInteger parse(final String value, final Unit unit) {
    try {
      final BigDecimal parsed = new BigDecimal(value);
      if (-1 == parsed.compareTo(BigDecimal.ZERO)) {
        throw new HerajException("Amount must be postive");
      }
      if (0 != parsed.compareTo(BigDecimal.ZERO)
          && -1 == parsed.compareTo(unit.getMinimum())) {
        throw new HerajException(
            String.format("Amount is smaller then minimum : %s %s",
                unit.getMinimum().toPlainString(), unit.getName()));
      }
      return parsed.multiply(unit.getRatio()).toBigInteger();
    } catch (HerajException e) {
      throw e;
    } catch (NumberFormatException e) {
      throw new HerajException(e);
    }
  }

  /**
   * Returns a Aer whose value is {@code (this.value + other.value)}.
   *
   * @param other value to be added to this aer.
   * @return {@code this.value + other.value}
   */
  public Aer add(final Aer other) {
    assertNotNull(other, "Other is null");
    assertNotEquals(Aer.EMPTY, this, "Cannot add to Aer.EMPTY");
    assertNotEquals(Aer.EMPTY, other, "Cannot add with Aer.EMPTY");
    if (null == this.value) {
      return other;
    }
    if (null == other.value) {
      return this;
    }
    return new Aer(this.value.add(other.value));
  }

  /**
   * Returns a Aer whose value is {@code (this.value - other.value)}. If a subtracted value is less
   * than 0, return 0 {@link Unit#AER}.
   *
   * @param other value to be added to this aer.
   * @return {@code this.value - other.value}
   */
  public Aer subtract(final Aer other) {
    assertNotNull(other, "Other is null");
    assertNotEquals(Aer.EMPTY, this, "Cannot subtract to Aer.EMPTY");
    assertNotEquals(Aer.EMPTY, other, "Cannot add with Aer.EMPTY");
    final BigInteger subtracted = this.value.subtract(other.value);
    return new Aer(subtracted.compareTo(BigInteger.ZERO) < 0 ? BigInteger.ZERO : subtracted);
  }

  /**
   * {@inheritDoc}.
   */
  @Override
  public int compareTo(final Aer other) {
    assertNotNull(other, "Other is null");
    return this.value.compareTo(other.value);
  }

  @Override
  public String toString() {
    if (null == this.value) {
      return EMPTY_STRING;
    }
    return toString(Unit.AER);
  }

  /**
   * Convert aer with {@code unit}.
   *
   * @param unit an aergo unit
   * @return an aer in {@code unit}
   */
  public String toString(final Unit unit) {
    assertNotNull(unit, "Unit must not null");
    if (null == this.value) {
      return EMPTY_STRING;
    }
    final BigDecimal origin = new BigDecimal(this.value);
    final BigDecimal ratio = unit.getRatio();
    final BigDecimal calculated = origin.divide(ratio);
    return String.format("%s(value=%s)", unit.getName(), calculated.toPlainString());
  }

}
