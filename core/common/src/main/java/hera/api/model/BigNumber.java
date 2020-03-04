/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.exception.HerajException;
import java.math.BigInteger;
import java.util.Map;
import lombok.EqualsAndHashCode;

@ApiAudience.Public
@ApiStability.Unstable
@EqualsAndHashCode
public class BigNumber {

  public static final String BIGNUM_JSON_KEY = "_bignum";
  protected static final String BIGNUM_JSON_FORM = "{ \"%s\": \"%s\" }";

  /**
   * Create {@code BigNumber}.
   * 
   * @param value a value
   * @return created {@code BigNumber}
   */
  public static BigNumber of(final String value) {
    return new BigNumber(value);
  }

  /**
   * Create {@code BigNumber}.
   * 
   * @param value a bigInteger
   * @return created {@code BigNumber}
   */
  public static BigNumber of(final BigInteger value) {
    return new BigNumber(value);
  }

  /**
   * Create {@code BigNumber} with a map. A map must have key {@link #BIGNUM_JSON_KEY} and be size
   * 1.
   * 
   * @param map a map
   * @return created {@code BigNumber}
   */
  public static BigNumber of(final Map<String, String> map) {
    return new BigNumber(map);
  }

  protected final BigInteger delegate;

  /**
   * Create {@code BigNumber}.
   * 
   * @param value a value
   */
  public BigNumber(final String value) {
    assertNotNull(value, "A biginteger value must not null");
    try {
      this.delegate = new BigInteger(value);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  /**
   * Create {@code BigNumber}.
   * 
   * @param value a bigInteger
   */
  public BigNumber(final BigInteger value) {
    assertNotNull(value, "A biginteger value must not null");
    this.delegate = value;
  }

  /**
   * Create {@code BigNumber} with a map. A map must have key {@link #BIGNUM_JSON_KEY} and be size
   * 1.
   * 
   * @param map a map
   */
  public BigNumber(final Map<String, String> map) {
    assertNotNull(map, "A map must not null");
    try {
      if (1 != map.size()) {
        throw new HerajException("Map size must be 1");
      }
      if (!map.containsKey(BIGNUM_JSON_KEY)) {
        throw new HerajException("No value for " + BIGNUM_JSON_KEY);
      }
      this.delegate = new BigInteger(map.get(BIGNUM_JSON_KEY));
    } catch (HerajException e) {
      throw e;
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  public String getValue() {
    return this.delegate.toString(10);
  }

  public BigNumber add(final BigNumber other) {
    return new BigNumber(this.delegate.add(other.delegate));
  }

  public BigNumber subtract(final BigNumber other) {
    return new BigNumber(this.delegate.subtract(other.delegate));
  }

  public BigNumber multiply(final BigNumber other) {
    return new BigNumber(this.delegate.multiply(other.delegate));
  }

  public BigNumber divide(final BigNumber other) {
    return new BigNumber(this.delegate.divide(other.delegate));
  }

  public String toJson() {
    return String.format(BIGNUM_JSON_FORM, BIGNUM_JSON_KEY, getValue());
  }

  @Override
  public String toString() {
    return "BigNumber(value=" + getValue() + ")";
  }

}
