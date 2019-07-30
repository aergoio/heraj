/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import java.math.BigInteger;
import lombok.EqualsAndHashCode;

@ApiAudience.Public
@ApiStability.Unstable
@EqualsAndHashCode
public class BigNumber {

  public static BigNumber of(final String value) {
    return new BigNumber(value);
  }

  protected final BigInteger delegate;

  public BigNumber(final String value) {
    this(new BigInteger(value));
  }

  protected BigNumber(final BigInteger value) {
    this.delegate = value;
  }

  public String getValue() {
    return delegate.toString(10);
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

  @Override
  public String toString() {
    return "BigNumber(value=" + delegate.toString() + ")";
  }

}
