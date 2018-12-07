/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;

import hera.exception.HerajException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class Signature {

  /**
   * Create {@link Signature}.
   *
   * @param sign sign value
   *
   * @return created signature
   */
  public static Signature of(final BytesValue sign) {
    final Signature signature = new Signature(sign);
    return signature;
  }

  @Getter
  protected final BytesValue sign;

  /**
   * Signature constructor.
   *
   * @param sign a sign value
   */
  public Signature(final BytesValue sign) {
    assertNotNull(sign, new HerajException("Sign value must not null"));
    this.sign = sign;
  }

}
