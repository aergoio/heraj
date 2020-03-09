/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;
import static java.util.Collections.unmodifiableMap;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.exception.HerajException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import lombok.EqualsAndHashCode;

@ApiAudience.Public
@ApiStability.Unstable
@EqualsAndHashCode
public class BigNumber {

  public static final String BIGNUM_JSON_KEY = "_bignum";
  public static final String BIGNUM_JSON_FORM = "{ \"" + BIGNUM_JSON_KEY + "\": \"%s\" }";

  /**
   * Create {@code BigNumber}.
   *
   * @param value a value. eg. 100
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
   * @param value a value. eg. 100
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
      final Object value = map.get(BIGNUM_JSON_KEY);
      if (!(value instanceof String)) {
        throw new HerajException("Value must be string");
      }
      this.delegate = new BigInteger((String) value);
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

  /**
   * Convert bignumber to a map corresponding to json format (key: {@link #BIGNUM_JSON_KEY}, value:
   * bignum value).
   *
   * @return a bignumber in map corresponding to json format
   */
  public Map<String, String> toMap() {
    final Map<String, String> map = new HashMap<>();
    map.put(BIGNUM_JSON_KEY, getValue());
    return unmodifiableMap(map);
  }

  @Override
  public String toString() {
    return "BigNumber(value=" + getValue() + ")";
  }

}
