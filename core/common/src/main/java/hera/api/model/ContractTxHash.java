/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.encode.Encodable;
import hera.exception.DecodingFailureException;
import hera.util.Adaptor;

@ApiAudience.Public
@ApiStability.Unstable
public class ContractTxHash extends TxHash implements Adaptor, Encodable {

  /**
   * Create {@code Hash} with a base58 encoded value.
   *
   * @param encoded String with base58 encoded
   * @return created {@link ContractTxHash}
   * @throws DecodingFailureException if decoding failed
   */
  @ApiAudience.Public
  public static ContractTxHash of(final String encoded) {
    return new ContractTxHash(encoded);
  }

  /**
   * Create {@code Hash}.
   *
   * @param bytesValue {@link BytesValue}
   * @return created {@link ContractTxHash}
   */
  @ApiAudience.Private
  public static ContractTxHash of(final BytesValue bytesValue) {
    return new ContractTxHash(bytesValue);
  }

  /**
   * ContractTxHash constructor.
   *
   * @param encoded String with base58 encoded
   * @throws DecodingFailureException if decoding failed
   */
  @ApiAudience.Public
  public ContractTxHash(final String encoded) {
    super(encoded);
  }

  /**
   * ContractTxHash constructor.
   *
   * @param bytesValue {@link BytesValue}
   */
  @ApiAudience.Private
  public ContractTxHash(final BytesValue bytesValue) {
    super(bytesValue);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T adapt(Class<T> adaptor) {
    if (adaptor.isAssignableFrom(ContractTxHash.class)) {
      return (T) this;
    } else if (adaptor.isAssignableFrom(BlockHash.class)) {
      return (T) BlockHash.of(getBytesValue());
    }
    return null;
  }

}
