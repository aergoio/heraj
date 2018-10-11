/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.VersionUtils;
import hera.api.encode.Base58WithCheckSum;
import hera.exception.InvalidVersionException;
import hera.util.Adaptor;
import hera.util.Base58Utils;
import java.util.Optional;
import lombok.Getter;

public class AccountAddress implements Adaptor {

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
    this(encoded.decode());
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
    final AccountAddress other = (AccountAddress) obj;
    return bytesValue.equals(other.bytesValue);
  }

  @Override
  public String toString() {
    return Base58Utils.encodeWithCheck(bytesValue.getValue());
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> Optional<T> adapt(Class<T> adaptor) {
    if (adaptor.isAssignableFrom(AccountAddress.class)) {
      return (Optional<T>) Optional.of(this);
    } else if (adaptor.isAssignableFrom(ContractAddress.class)) {
      return (Optional<T>) Optional.ofNullable(ContractAddress.of(getBytesValue()));
    }
    return Optional.empty();
  }

}
