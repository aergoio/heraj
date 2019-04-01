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
public class ChainIdHash extends Hash implements Adaptor, Encodable {

  /**
   * Create {@code ChainIdHash} with a base58 encoded value.
   *
   * @param encoded String with base58 encoded
   * @return created {@link ChainIdHash}
   * @throws DecodingFailureException if decoding failed
   */
  @ApiAudience.Public
  public static ChainIdHash of(final String encoded) {
    return new ChainIdHash(encoded);
  }

  /**
   * Create {@code Hash}.
   *
   * @param bytesValue {@link BytesValue}
   * @return created {@link ChainIdHash}
   */
  @ApiAudience.Private
  public static ChainIdHash of(final BytesValue bytesValue) {
    return new ChainIdHash(bytesValue);
  }

  /**
   * BlockHash constructor.
   *
   * @param encoded String with base58 encoded
   * @throws DecodingFailureException if decoding failed
   */
  @ApiAudience.Public
  public ChainIdHash(final String encoded) {
    super(encoded);
  }

  /**
   * BlockHash constructor.
   *
   * @param bytesValue {@link BytesValue}
   */
  @ApiAudience.Private
  public ChainIdHash(final BytesValue bytesValue) {
    super(bytesValue);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T adapt(Class<T> adaptor) {
    if (adaptor.isAssignableFrom(ChainIdHash.class)) {
      return (T) this;
    } else if (adaptor.isAssignableFrom(BlockHash.class)) {
      return (T) BlockHash.of(getBytesValue());
    } else if (adaptor.isAssignableFrom(TxHash.class)) {
      return (T) TxHash.of(getBytesValue());
    }
    return null;
  }

}