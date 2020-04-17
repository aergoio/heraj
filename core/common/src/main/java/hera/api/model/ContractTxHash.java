/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.util.Adaptor;

@ApiAudience.Public
@ApiStability.Unstable
public class ContractTxHash extends TxHash implements Adaptor, Encodable {

  /**
   * Create {@code ContractTxHash} with a base58 encoded one.
   *
   * @param encoded an base58 encoded contract tx hash
   * @return created {@link ContractTxHash}
   */
  public static ContractTxHash of(final String encoded) {
    return new ContractTxHash(encoded);
  }

  /**
   * Create {@code ContractTxHash} with a bytes value.
   *
   * @param bytesValue a bytesValue
   * @return created {@link ContractTxHash}
   */
  public static ContractTxHash of(final BytesValue bytesValue) {
    return new ContractTxHash(bytesValue);
  }

  /**
   * Create {@code ContractTxHash} with a base58 encoded one.
   *
   * @param encoded an base58 encoded contract tx hash
   */
  public ContractTxHash(final String encoded) {
    super(encoded);
  }

  /**
   * Create {@code ContractTxHash} with a bytes value.
   *
   * @param bytesValue a bytesValue
   */
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
