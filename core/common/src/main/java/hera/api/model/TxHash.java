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
public class TxHash extends Hash implements Adaptor, Encodable {

  /**
   * Create {@code Hash} with a base58 encoded value.
   *
   * @param encoded Base58 with checksum encoded
   * @return created {@link TxHash}
   * @throws DecodingFailureException if decoding failed
   */
  @ApiAudience.Public
  public static TxHash of(final String encoded) {
    return new TxHash(encoded);
  }

  public static TxHash of(final BytesValue bytesValue) {
    return new TxHash(bytesValue);
  }

  /**
   * TxHash constructor.
   *
   * @param encoded Base58 encoded value
   * @throws DecodingFailureException if decoding failed
   */
  @ApiAudience.Public
  public TxHash(final String encoded) {
    super(encoded);
  }

  @ApiAudience.Private
  public TxHash(final BytesValue bytesValue) {
    super(bytesValue);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T adapt(Class<T> adaptor) {
    if (adaptor.isAssignableFrom(TxHash.class)) {
      return (T) this;
    } else if (adaptor.isAssignableFrom(BlockHash.class)) {
      return (T) BlockHash.of(getBytesValue());
    } else if (adaptor.isAssignableFrom(ContractTxHash.class)) {
      return (T) ContractTxHash.of(getBytesValue());
    }
    return null;
  }

}
