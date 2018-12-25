/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.exception.HerajException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ApiAudience.Public
@ApiStability.Unstable
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
  @ApiAudience.Private
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
  @ApiAudience.Private
  public Signature(final BytesValue sign) {
    assertNotNull(sign, new HerajException("Sign value must not null"));
    this.sign = sign;
  }

}
