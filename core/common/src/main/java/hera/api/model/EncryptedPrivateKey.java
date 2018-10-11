/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.VersionUtils;
import hera.api.encode.Base58WithCheckSum;
import hera.exception.InvalidVersionException;
import hera.util.Base58Utils;
import lombok.Getter;

public class EncryptedPrivateKey {

  public static final byte VERSION = (byte) 0xAA;

  /**
   * Create {@code EncryptedPrivateKey} with a base58 with checksum encoded value.
   *
   * @param encoded a base58 with checksum encoded encoded value
   * @return created {@link EncryptedPrivateKey}
   *
   * @throws InvalidVersionException when address version mismatch
   */
  public static EncryptedPrivateKey of(final Base58WithCheckSum encoded) {
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
   * @throws InvalidVersionException when address version mismatch
   */
  public EncryptedPrivateKey(final Base58WithCheckSum encoded) {
    this(encoded.decode());
  }

  /**
   * EncryptedPrivateKey constructor.
   *
   * @param bytesValue {@link BytesValue}
   *
   * @throws InvalidVersionException when address version mismatch
   */
  public EncryptedPrivateKey(final BytesValue bytesValue) {
    if (BytesValue.EMPTY != bytesValue) {
      final byte[] rawBytes = bytesValue.getValue();
      VersionUtils.validate(rawBytes, VERSION);
    }
    this.bytesValue = bytesValue;
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
    return Base58Utils.encodeWithCheck(bytesValue.getValue());
  }

}
