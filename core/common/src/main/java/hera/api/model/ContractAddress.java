/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;

@ApiAudience.Public
@ApiStability.Unstable
public class ContractAddress extends AccountAddress {

  public static final ContractAddress EMPTY = new ContractAddress();

  /**
   * Create {@code ContractAddress} with a base58 with checksum encoded value.
   *
   * @param encoded a base58 with checksum encoded account address
   * @return created {@link ContractAddress}
   */
  public static ContractAddress of(final String encoded) {
    return new ContractAddress(encoded);
  }

  /**
   * Create {@code ContractAddress} with a bytes value. Note that bytes value doesn't have a prefix.
   *
   * @param bytesValue a bytes value
   * @return created {@link ContractAddress}
   */
  public static ContractAddress of(final BytesValue bytesValue) {
    return new ContractAddress(bytesValue);
  }

  /**
   * Create {@code ContractAddress} with a base58 with checksum encoded value.
   *
   * @param encoded a base58 with checksum encoded account address
   */
  public ContractAddress(final String encoded) {
    super(encoded);
  }

  /**
   * Create {@code ContractAddress} with a bytes value. Note that bytes value doesn't have a prefix.
   *
   * @param bytesValue a bytes value
   */
  public ContractAddress(final BytesValue bytesValue) {
    super(bytesValue);
  }

  ContractAddress() {
    super();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T adapt(final Class<T> adaptor) {
    if (adaptor.isAssignableFrom(ContractAddress.class)) {
      return (T) this;
    } else if (adaptor.isAssignableFrom(AccountAddress.class)) {
      return (T) AccountAddress.of(getBytesValue());
    }
    return null;
  }

}
