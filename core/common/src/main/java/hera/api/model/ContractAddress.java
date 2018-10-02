/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.api.encode.Encoded;
import hera.exception.InvalidVersionException;
import java.util.Optional;

public class ContractAddress extends AccountAddress {

  /**
   * Create {@code ContractAddress} with an encoded value.
   *
   * @param encoded an encoded value
   * @return created {@link ContractAddress}
   *
   * @throws InvalidVersionException when address version mismatch
   */
  public static ContractAddress of(final Encoded encoded) {
    return new ContractAddress(encoded);
  }

  /**
   * Create {@code ContractAddress}.
   *
   * @param bytesValue {@link BytesValue}
   * @return created {@link ContractAddress}
   *
   * @throws InvalidVersionException when address version mismatch
   */
  public static ContractAddress of(final BytesValue bytesValue) {
    return new ContractAddress(bytesValue);
  }

  /**
   * ContractAddress constructor.
   *
   * @param encoded an encoded value
   *
   * @throws InvalidVersionException when address version mismatch
   */
  public ContractAddress(final Encoded encoded) {
    super(encoded);
  }

  /**
   * ContractAddress constructor.
   *
   * @param bytesValue {@link BytesValue}
   *
   * @throws InvalidVersionException when address version mismatch
   */
  public ContractAddress(final BytesValue bytesValue) {
    super(bytesValue);
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
