/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.util.Base58Utils;
import java.util.Arrays;
import java.util.Optional;

public class ContractAddress extends AccountAddress {

  /**
   * Create {@code ContractAddress} with a raw bytes array.
   *
   * @param bytes value
   * @return created {@link ContractAddress}
   */
  public static ContractAddress of(final byte[] bytes) {
    return of(bytes, ContractAddress::new);
  }

  /**
   * Create {@code ContractAddress} with a base58 encoded value.
   *
   * @param encoded base58 encoded value
   * @return created {@link ContractAddress}
   */
  public static ContractAddress of(final String encoded) {
    return of(encoded, e -> {
      final byte[] withVersion = Base58Utils.decodeWithCheck(encoded);
      return Arrays.copyOfRange(withVersion, 1, withVersion.length);
    }, ContractAddress::new);
  }

  public ContractAddress(final byte[] value) {
    super(value);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> Optional<T> adapt(Class<T> adaptor) {
    if (adaptor.isAssignableFrom(ContractAddress.class)) {
      return (Optional<T>) Optional.of(this);
    }
    return Optional.empty();
  }

}
