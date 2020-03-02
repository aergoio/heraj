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
public class BlockHash extends Hash implements Adaptor, Encodable {

  /**
   * Create {@code BlockHash} with a base58 encoded one.
   *
   * @param encoded an base58 encoded block hash
   * @return created {@link BlockHash}
   */
  public static BlockHash of(final String encoded) {
    return new BlockHash(encoded);
  }

  /**
   * Create {@code BlockHash} with a bytes value.
   *
   * @param bytesValue a bytesValue
   * @return created {@link BlockHash}
   */
  public static BlockHash of(final BytesValue bytesValue) {
    return new BlockHash(bytesValue);
  }

  /**
   * Create {@code BlockHash} with a base58 encoded one.
   *
   * @param encoded an base58 encoded block hash
   */
  public BlockHash(final String encoded) {
    super(encoded);
  }

  /**
   * Create {@code BlockHash} with a bytes value.
   *
   * @param bytesValue a bytesValue
   */
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
