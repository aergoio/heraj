/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.encode.Encodable;
import hera.api.encode.Encoder;
import java.util.concurrent.atomic.AtomicStampedReference;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@ApiAudience.Public
@ApiStability.Unstable
@EqualsAndHashCode
@Builder(builderMethodName = "newBuilder")
public class Signature implements Encodable {

  public static final Signature EMPTY = new Signature();

  /**
   * Create {@code Signature}.
   *
   * @param sign a sign value
   * @return created {@code Signature}.
   */
  public static Signature of(final BytesValue sign) {
    return new Signature(sign);
  }

  // keep builder for backward compatibility
  @NonNull
  @Getter
  protected final BytesValue sign;

  /**
   * Create {@code Signature}.
   *
   * @param sign a sign value
   */
  public Signature(final BytesValue sign) {
    assertNotNull(sign, "Sign must not null");
    this.sign = sign;
  }

  protected Signature() {
    this.sign = BytesValue.EMPTY;
  }

  @Override
  public String getEncoded() {
    return sign.getEncoded(Encoder.Base58);
  }

  @Override
  public String toString() {
    return String.format("Signature(sign=%s)", sign.getEncoded(Encoder.Base58));
  }

}
