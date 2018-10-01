/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.exception.InvalidVersionException;
import hera.util.Base58Utils;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class AccountAddress extends BytesValue {

  public static final byte ADDRESS_VERSION = 0x42;

  /**
   * Create {@code AccountAddress} with a raw bytes array.
   *
   * @param rawBytes value
   * @return created {@link AccountAddress}
   */
  public static AccountAddress of(final byte[] rawBytes) {
    return of(rawBytes, AccountAddress::new);
  }

  /**
   * Create {@code AccountAddress} with a base58 with checksum encoded value.
   *
   * @param encoded base58 encoded value
   * @return created {@link AccountAddress}
   */
  public static AccountAddress of(final String encoded) {
    return of(encoded, e -> {
      final byte[] withVersion = Base58Utils.decodeWithCheck(encoded);
      if (ADDRESS_VERSION != withVersion[0]) {
        throw new InvalidVersionException(ADDRESS_VERSION, withVersion[0]);
      }
      return Arrays.copyOfRange(withVersion, 1, withVersion.length);
    }, AccountAddress::new);
  }

  public AccountAddress(final byte[] value) {
    super(value);
  }

  @Override
  public String getEncodedValue() throws IOException {
    final byte[] withVersion = new byte[value.length + 1];
    withVersion[0] = ADDRESS_VERSION;
    System.arraycopy(value, 0, withVersion, 1, value.length);
    return Base58Utils.encodeWithCheck(withVersion);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> Optional<T> adapt(Class<T> adaptor) {
    if (adaptor.isAssignableFrom(AccountAddress.class)) {
      return (Optional<T>) Optional.of(this);
    }
    return Optional.empty();
  }

}
