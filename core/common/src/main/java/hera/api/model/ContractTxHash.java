/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import java.util.Optional;

public class ContractTxHash extends Hash {

  /**
   * Create {@code ContractTxHash} with a raw bytes array.
   *
   * @param bytes value
   * @return created {@link ContractTxHash}
   */
  public static ContractTxHash of(final byte[] bytes) {
    return of(bytes, ContractTxHash::new);
  }

  /**
   * Create {@code ContractTxHash} with a base58 encoded value.
   *
   * @param encoded base58 encoded value
   * @return created {@link ContractTxHash}
   */
  public static ContractTxHash of(final String encoded) {
    return of(encoded, ContractTxHash::new);
  }

  public ContractTxHash(final byte[] value) {
    super(value);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> Optional<T> adapt(Class<T> adaptor) {
    if (adaptor.isAssignableFrom(ContractTxHash.class)) {
      return (Optional<T>) Optional.of(this);
    } else if (adaptor.isAssignableFrom(Hash.class)) {
      return (Optional<T>) Optional.ofNullable(Hash.of(getValue()));
    } else if (adaptor.isAssignableFrom(TxHash.class)) {
      return (Optional<T>) Optional.ofNullable(TxHash.of(getValue()));
    } else if (adaptor.isAssignableFrom(BlockHash.class)) {
      return (Optional<T>) Optional.ofNullable(BlockHash.of(getValue()));
    }
    return Optional.empty();
  }

}
