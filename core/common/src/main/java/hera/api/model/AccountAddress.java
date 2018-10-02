/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.api.encode.Encoded;
import hera.exception.InvalidVersionException;
import hera.util.Adaptor;
import hera.util.Base58Utils;
import java.util.Arrays;
import java.util.Optional;
import lombok.Getter;

public class AccountAddress implements Adaptor {

  public static final byte ADDRESS_VERSION = 0x42;

  /**
   * Create {@code AccountAddress} with a base58 with checksum encoded value.
   *
   * @param encoded an encoded value
   * @return created {@link AccountAddress}
   *
   * @throws InvalidVersionException when address version mismatch
   */
  public static AccountAddress of(final Encoded encoded) {
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
   * @param encoded an encoded value
   *
   * @throws InvalidVersionException when address version mismatch
   */
  public AccountAddress(final Encoded encoded) {
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
      if (ADDRESS_VERSION != rawBytes[0]) {
        throw new InvalidVersionException(ADDRESS_VERSION, rawBytes[0]);
      }
    }
    this.bytesValue = bytesValue;
  }

  /**
   * Get {@code BytesValue} without version byte.
   *
   * @return bytesValue
   */
  public BytesValue getBytesValueWithoutVersion() {
    if (BytesValue.EMPTY == bytesValue) {
      return bytesValue;
    }
    final byte[] withVersion = bytesValue.getValue();
    return BytesValue.of(Arrays.copyOfRange(withVersion, 1, withVersion.length));
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
