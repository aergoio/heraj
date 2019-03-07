/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.EncodingUtils.decodeBase58WithCheck;
import static hera.util.EncodingUtils.encodeBase58WithCheck;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.encode.Encodable;
import hera.exception.DecodingFailureException;
import hera.exception.InvalidVersionException;
import hera.spec.EncryptedPrivateKeySpec;
import hera.util.VersionUtils;
import lombok.Getter;

@ApiAudience.Public
@ApiStability.Unstable
public class EncryptedPrivateKey implements Encodable {

  /**
   * Create {@code EncryptedPrivateKey} with a base58 with checksum encoded value.
   *
   * @param encoded a base58 with checksum encoded encoded value
   * @return created {@link EncryptedPrivateKey}
   *
   * @throws DecodingFailureException if decoding failed
   * @throws InvalidVersionException when address version mismatch
   */
  @ApiAudience.Public
  public static EncryptedPrivateKey of(final String encoded) {
    return new EncryptedPrivateKey(encoded);
  }

  /**
   * Create {@code EncryptedPrivateKey}.
   *
   * @param bytesValue {@link BytesValue}
   * @return created {@link EncryptedPrivateKey}
   *
   * @throws InvalidVersionException when address version mismatch
   */
  @ApiAudience.Private
  public static EncryptedPrivateKey of(final BytesValue bytesValue) {
    return new EncryptedPrivateKey(bytesValue);
  }

  @Getter
  protected final BytesValue bytesValue;

  /**
   * EncryptedPrivateKey constructor.
   *
   * @param encoded a base58 with checksum encoded encoded value
   *
   * @throws DecodingFailureException if decoding failed
   * @throws InvalidVersionException when address version mismatch
   */
  @ApiAudience.Public
  public EncryptedPrivateKey(final String encoded) {
    this(decodeBase58WithCheck(encoded));
  }

  /**
   * EncryptedPrivateKey constructor.
   *
   * @param bytesValue {@link BytesValue}
   *
   * @throws InvalidVersionException when address version mismatch
   */
  @ApiAudience.Private
  public EncryptedPrivateKey(final BytesValue bytesValue) {
    if (BytesValue.EMPTY != bytesValue) {
      VersionUtils.validate(bytesValue, EncryptedPrivateKeySpec.PREFIX);
    }
    this.bytesValue = bytesValue;
  }

  @Override
  public String getEncoded() {
    return encodeBase58WithCheck(getBytesValue());
  }

  @Override
  public int hashCode() {
    return bytesValue.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (null == obj) {
      return false;
    }
    if (!obj.getClass().equals(getClass())) {
      return false;
    }
    final EncryptedPrivateKey other = (EncryptedPrivateKey) obj;
    return bytesValue.equals(other.bytesValue);
  }

  @Override
  public String toString() {
    return getEncoded();
  }

}
