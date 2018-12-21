/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.exception.InvalidVersionException;

public class ContractAddress extends AccountAddress {

  /**
   * Create {@code ContractAddress} with a base58 with checksum encoded value.
   *
   * @param encoded a base58 with checksum encoded encoded value
   * @return created {@link ContractAddress}
   *
   * @throws InvalidVersionException when address version mismatch
   */
  public static ContractAddress of(final String encoded) {
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
   * @param encoded a base58 with checksum encoded encoded value
   *
   * @throws InvalidVersionException when address version mismatch
   */
  public ContractAddress(final String encoded) {
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

}
