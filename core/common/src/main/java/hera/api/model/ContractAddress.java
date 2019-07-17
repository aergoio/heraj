/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.exception.DecodingFailureException;
import hera.util.StringUtils;

@ApiAudience.Public
@ApiStability.Unstable
public class ContractAddress extends AccountAddress {

  public static final ContractAddress EMPTY =
      new ContractAddress(BytesValue.EMPTY, StringUtils.EMPTY_STRING);

  /**
   * Create {@code ContractAddress} with a base58 with checksum encoded value.
   *
   * @param encoded a base58 with checksum encoded encoded value
   * @return created {@link ContractAddress}
   *
   * @throws DecodingFailureException if decoding failed
   */
  @ApiAudience.Public
  public static ContractAddress of(final String encoded) {
    return new ContractAddress(encoded);
  }

  /**
   * Create {@code ContractAddress}.
   *
   * @param bytesValue {@link BytesValue}
   * @return created {@link ContractAddress}
   *
   */
  @ApiAudience.Private
  public static ContractAddress of(final BytesValue bytesValue) {
    return new ContractAddress(bytesValue);
  }

  /**
   * ContractAddress constructor.
   *
   * @param encoded a base58 with checksum encoded encoded value
   *
   * @throws DecodingFailureException if decoding failed
   */
  @ApiAudience.Public
  public ContractAddress(final String encoded) {
    super(encoded);
  }

  /**
   * ContractAddress constructor.
   *
   * @param bytesValue {@link BytesValue}
   *
   */
  @ApiAudience.Private
  public ContractAddress(final BytesValue bytesValue) {
    super(bytesValue);
  }

  protected ContractAddress(final BytesValue bytesValue, final String value) {
    super(bytesValue, value);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T adapt(Class<T> adaptor) {
    if (adaptor.isAssignableFrom(ContractAddress.class)) {
      return (T) this;
    }
    return null;
  }

}
