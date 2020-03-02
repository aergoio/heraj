/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.encode.Encodable;
import hera.util.Adaptor;

@ApiAudience.Public
@ApiStability.Unstable
public class ChainIdHash extends Hash implements Adaptor, Encodable {

  /**
   * Create {@code ChainIdHash} with a base58 encoded one.
   *
   * @param encoded String with base58 encoded chain id hash
   * @return created {@link ChainIdHash}
   */
  public static ChainIdHash of(final String encoded) {
    return new ChainIdHash(encoded);
  }

  /**
   * Create {@code BlockHash} with a bytes value.
   *
   * @param bytesValue a bytes value
   * @return created {@link ChainIdHash}
   */
  public static ChainIdHash of(final BytesValue bytesValue) {
    return new ChainIdHash(bytesValue);
  }

  /**
   * Create {@code ChainIdHash} with a base58 encoded one.
   *
   * @param encoded String with base58 encoded chain id hash
   */
  public ChainIdHash(final String encoded) {
    super(encoded);
  }

  /**
   * Create {@code BlockHash} with a bytes value.
   *
   * @param bytesValue a bytes value
   */
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
