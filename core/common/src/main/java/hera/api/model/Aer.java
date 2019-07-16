/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.exception.InvalidAerAmountException;
import hera.spec.AergoSpec;
import java.math.BigDecimal;
import java.math.BigInteger;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ApiAudience.Public
@ApiStability.Unstable
@ToString
@EqualsAndHashCode
public class Aer implements Comparable<Aer> {

  public static final Aer EMPTY = new Aer();

  public static final Aer ZERO = new Aer(BigInteger.valueOf(0L));

  public static final Aer ONE = new Aer(BigInteger.valueOf(1L));

  public static final Aer GIGA_ONE = new Aer("1", Unit.GAER);

  public static final Aer AERGO_ONE = new Aer("1", Unit.AERGO);

  /**
   * Create {@code Aer} instance.
   *
   * @param amount an amount in string which is considered as {@link Unit#AER}
   * @return an aergo instance
   * @throws InvalidAerAmountException if an amount is invalid
   */
  @ApiAudience.Public
  public static Aer of(final String amount) {
    return new Aer(amount);
  }

  /**
   * Create {@code Aer} instance.
   *
   * @param amount an amount in string
   * @param unit an unit {@link Unit}
   * @return an aergo instance
   * @throws InvalidAerAmountException if an amount is invalid
   */
  @ApiAudience.Public
  public static Aer of(final String amount, final Unit unit) {
    return new Aer(amount, unit);
  }

  /**
   * Create an {@code Aer} instance.
   *
   * @param amount an amount in {@link Unit#AER}
   * @return an aergo instance
   */
  @ApiAudience.Public
  public static Aer of(final BigInteger amount) {
    return new Aer(amount);
  }

  /**
   * An unit to represent aergo. Consist of {@code AER, GAER, AERGO}.
   */
  @ApiAudience.Public
  @RequiredArgsConstructor
  public enum Unit {
    AER(AergoSpec.Unit.AER), GAER(AergoSpec.Unit.GAER), AERGO(AergoSpec.Unit.AERGO);

    @Getter
    final AergoSpec.Unit delegate;
  }

  @Getter
  protected final BigInteger value;

  protected Aer() {
    this.value = null;
  }

  /**
   * Create {@code Aer} instance.
   *
   * @param amount an amount in string which is considered as {@link Unit#AER}
   * @throws InvalidAerAmountException if an amount is invalid
   */
  @ApiAudience.Public
  public Aer(final String amount) {
    this(amount, Unit.AER);
  }

  /**
   * Create {@code Aer} instance.
   *
   * @param amount an amount in string
   * @param unit an unit {@link Unit}
   * @throws InvalidAerAmountException if an amount is invalid
   */
  @ApiAudience.Public
  public Aer(final String amount, final Unit unit) {
    assertNotNull(amount, "Amount must not null");
    assertNotNull(unit, "Unit must not null");
    this.value = parse(amount, unit);
  }

  /**
   * Aer constructor.
   *
   * @param amount an amount in {@link Unit#AER}
   */
  @ApiAudience.Public
  public Aer(final BigInteger amount) {
    this.value =
        null != amount ? (amount.compareTo(BigInteger.ZERO) >= 0 ? amount : BigInteger.ZERO)
            : BigInteger.ZERO;
  }

  protected BigInteger parse(final String value, final Unit unit) {
    try {
      final BigDecimal parsedValue = new BigDecimal(value);
      if (parsedValue.compareTo(BigDecimal.ZERO) == -1) {
        throw new InvalidAerAmountException("Amount should be postive");
      }
      if (parsedValue.compareTo(BigDecimal.ZERO) != 0
          && parsedValue.compareTo(unit.getDelegate().getMinimum()) == -1) {
        throw new InvalidAerAmountException(
            String.format("Amount is smaller then minimum : %s %s",
                unit.getDelegate().getMinimum().toPlainString(), unit.getDelegate().getName()));
      }
      return parsedValue.multiply(unit.getDelegate().getTimesToAer()).toBigInteger();
    } catch (NumberFormatException e) {
      throw new InvalidAerAmountException(e);
    }
  }

  /**
   * Returns a Aer whose value is {@code (this.value + other.value)}.
   *
   * @param other value to be added to this aer.
   * @return {@code this.value + other.value}
   */
  public Aer add(final Aer other) {
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
    final BigInteger subtracted = this.value.subtract(other.value);
    return new Aer(subtracted.compareTo(BigInteger.ZERO) < 0 ? BigInteger.ZERO : subtracted);
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
