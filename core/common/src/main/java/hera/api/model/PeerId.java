/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.EncodingUtils.decodeBase58;
import static hera.util.EncodingUtils.encodeBase58;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.encode.Encodable;
import hera.exception.DecodingFailureException;
import lombok.Getter;

@ApiAudience.Public
@ApiStability.Unstable
public class PeerId implements Encodable {

  /**
   * Create {@code PeerId} with a base58 encoded value.
   *
   * @param encoded a base58 encoded encoded value
   * @return created {@link PeerId}
   *
   * @throws DecodingFailureException if decoding failed
   */
  @ApiAudience.Public
  public static PeerId of(final String encoded) {
    return new PeerId(encoded);
  }

  /**
   * Create {@code PeerId}.
   *
   * @param bytesValue {@link BytesValue}
   * @return created {@link PeerId}
   */
  @ApiAudience.Private
  public static PeerId of(final BytesValue bytesValue) {
    return new PeerId(bytesValue);
  }

  @Getter
  protected final BytesValue bytesValue;

  /**
   * PeerId constructor.
   *
   * @param encoded a base58 with encoded value
   *
   * @throws DecodingFailureException if decoding failed
   */
  @ApiAudience.Public
  public PeerId(final String encoded) {
    this(decodeBase58(encoded));
  }

  /**
   * PeerId constructor.
   *
   * @param bytesValue {@link BytesValue}
   */
  @ApiAudience.Private
  public PeerId(final BytesValue bytesValue) {
    this.bytesValue = bytesValue;
  }

  @Override
  public String getEncoded() {
    return encodeBase58(getBytesValue());
  }

  @Override
  public int hashCode() {
    return this.bytesValue.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (null == obj) {
      return false;
    }
    if (!obj.getClass().equals(getClass())) {
      return false;
    }
    final PeerId other = (PeerId) obj;
    return this.bytesValue.equals(other.bytesValue);
  }

  @Override
  public String toString() {
    return getEncoded();
  }

}
