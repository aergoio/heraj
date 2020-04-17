/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.util.Adaptor;

@ApiAudience.Public
@ApiStability.Unstable
public class TxHash extends Hash implements Adaptor, Encodable {

  /**
   * Create {@code TxHash} with a base58 encoded one.
   *
   * @param encoded an base58 encoded tx hash
   * @return created {@link TxHash}
   */
  public static TxHash of(final String encoded) {
    return new TxHash(encoded);
  }

  /**
   * Create {@code TxHash} with a bytes value.
   *
   * @param bytesValue a bytesValue
   * @return created {@link TxHash}
   */
  public static TxHash of(final BytesValue bytesValue) {
    return new TxHash(bytesValue);
  }

  /**
   * Create {@code TxHash} with a base58 encoded one.
   *
   * @param encoded an base58 encoded tx hash
   */
  public TxHash(final String encoded) {
    super(encoded);
  }

  /**
   * Create {@code TxHash} with a bytes value.
   *
   * @param bytesValue a bytesValue
   */
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
