/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.api.encode.Base58;
import java.util.Optional;

public class TxHash extends Hash {

  /**
   * Create {@code Hash} with a base58 encoded value.
   *
   * @param encoded Base58 with checksum encoded
   * @return created {@link TxHash}
   */
  public static TxHash of(final Base58 encoded) {
    return new TxHash(encoded);
  }

  /**
   * Create {@code Hash}.
   *
   * @param bytesValue {@link BytesValue}
   * @return created {@link TxHash}
   */
  public static TxHash of(final BytesValue bytesValue) {
    return new TxHash(bytesValue);
  }

  /**
   * TxHash constructor.
   *
   * @param encoded Base58 encoded value
   */
  public TxHash(final Base58 encoded) {
    super(encoded);
  }

  /**
   * TxHash constructor.
   *
   * @param bytesValue {@link BytesValue}
   */
  public TxHash(final BytesValue bytesValue) {
    super(bytesValue);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> Optional<T> adapt(Class<T> adaptor) {
    if (adaptor.isAssignableFrom(TxHash.class)) {
      return (Optional<T>) Optional.of(this);
    } else if (adaptor.isAssignableFrom(BlockHash.class)) {
      return (Optional<T>) Optional.of(BlockHash.of(getBytesValue()));
    } else if (adaptor.isAssignableFrom(ContractTxHash.class)) {
      return (Optional<T>) Optional.of(ContractTxHash.of(getBytesValue()));
    }
    return Optional.empty();
  }

}
