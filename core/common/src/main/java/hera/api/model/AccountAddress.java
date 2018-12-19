/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.api.encode.Base58WithCheckSum;
import hera.exception.HerajException;
import hera.exception.InvalidVersionException;
import hera.util.Base58Utils;
import hera.util.VersionUtils;
import java.io.IOException;
import lombok.Getter;

public class AccountAddress {

  public static final byte VERSION = 0x42;

  /**
   * Create {@code AccountAddress} with a base58 with checksum encoded value.
   *
   * @param encoded a base58 with checksum encoded encoded value
   * @return created {@link AccountAddress}
   *
   * @throws InvalidVersionException when address version mismatch
   */
  public static AccountAddress of(final Base58WithCheckSum encoded) {
    return new AccountAddress(encoded);
  }

  /**
   * Create {@code AccountAddress}.
   *
   * @param bytesValue {@link BytesValue}
   * @return created {@link AccountAddress}
   *
   * @throws InvalidVersionException when address version mismatch
   */
  public static AccountAddress of(final BytesValue bytesValue) {
    return new AccountAddress(bytesValue);
  }

  @Getter
  protected final BytesValue bytesValue;

  /**
   * AccountAddress constructor.
   *
   * @param encoded a base58 with checksum encoded encoded value
   *
   * @throws InvalidVersionException when address version mismatch
   */
  public AccountAddress(final Base58WithCheckSum encoded) {
    try {
      final BytesValue decoded = encoded.decode();
      VersionUtils.validate(decoded, VERSION);
      this.bytesValue = decoded;
    } catch (IOException e) {
      throw new HerajException(e);
    }
  }

  /**
   * AccountAddress constructor.
   *
   * @param bytesValue {@link BytesValue}
   *
   * @throws InvalidVersionException when address version mismatch
   */
  public AccountAddress(final BytesValue bytesValue) {
    if (BytesValue.EMPTY != bytesValue) {
      VersionUtils.validate(bytesValue, VERSION);
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
    final AccountAddress other = (AccountAddress) obj;
    return bytesValue.equals(other.bytesValue);
  }

  @Override
  public String toString() {
    return Base58Utils.encodeWithCheck(bytesValue.getValue());
  }

}
