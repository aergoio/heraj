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
public class BlockHash extends Hash implements Adaptor, Encodable {

  /**
   * Create {@code Hash} with a base58 encoded value.
   *
   * @param encoded String with base58 encoded
   * @return created {@link BlockHash}
   * @throws DecodingFailureException if decoding failed
   */
  @ApiAudience.Public
  public static BlockHash of(final String encoded) {
    return new BlockHash(encoded);
  }

  /**
   * Create {@code Hash}.
   *
   * @param bytesValue {@link BytesValue}
   * @return created {@link BlockHash}
   */
  @ApiAudience.Private
  public static BlockHash of(final BytesValue bytesValue) {
    return new BlockHash(bytesValue);
  }

  /**
   * BlockHash constructor.
   *
   * @param encoded String with base58 encoded
   * @throws DecodingFailureException if decoding failed
   */
  @ApiAudience.Public
  public BlockHash(final String encoded) {
    super(encoded);
  }

  /**
   * BlockHash constructor.
   *
   * @param bytesValue {@link BytesValue}
   */
  @ApiAudience.Private
  public BlockHash(final BytesValue bytesValue) {
    super(bytesValue);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T adapt(Class<T> adaptor) {
    if (adaptor.isAssignableFrom(BlockHash.class)) {
      return (T) this;
    } else if (adaptor.isAssignableFrom(TxHash.class)) {
      return (T) TxHash.of(getBytesValue());
    } else if (adaptor.isAssignableFrom(ContractTxHash.class)) {
      return (T) ContractTxHash.of(getBytesValue());
    }
    return null;
  }

}
