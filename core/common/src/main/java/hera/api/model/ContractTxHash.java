/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.api.encode.Base58;
import java.util.Optional;

public class ContractTxHash extends TxHash {

  /**
   * Create {@code Hash} with a base58 encoded value.
   *
   * @param encoded Base58 with checksum encoded
   * @return created {@link ContractTxHash}
   */
  public static ContractTxHash of(final Base58 encoded) {
    return new ContractTxHash(encoded);
  }

  /**
   * Create {@code Hash}.
   *
   * @param bytesValue {@link BytesValue}
   * @return created {@link ContractTxHash}
   */
  public static ContractTxHash of(final BytesValue bytesValue) {
    return new ContractTxHash(bytesValue);
  }

  /**
   * ContractTxHash constructor.
   *
   * @param encoded Base58 encoded value
   */
  public ContractTxHash(final Base58 encoded) {
    super(encoded);
  }

  /**
   * ContractTxHash constructor.
   *
   * @param bytesValue {@link BytesValue}
   */
  public ContractTxHash(final BytesValue bytesValue) {
    super(bytesValue);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> Optional<T> adapt(Class<T> adaptor) {
    if (adaptor.isAssignableFrom(ContractTxHash.class)) {
      return (Optional<T>) Optional.of(this);
    } else if (adaptor.isAssignableFrom(BlockHash.class)) {
      return (Optional<T>) Optional.ofNullable(BlockHash.of(getBytesValue()));
    }
    return Optional.empty();
  }

}
