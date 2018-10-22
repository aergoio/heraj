/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.api.encode.Base58;
import java.util.Optional;

public class BlockHash extends Hash {

  /**
   * Create {@code Hash} with a base58 encoded value.
   *
   * @param encoded Base58 with checksum encoded
   * @return created {@link BlockHash}
   */
  public static BlockHash of(final Base58 encoded) {
    return new BlockHash(encoded);
  }

  /**
   * Create {@code Hash}.
   *
   * @param bytesValue {@link BytesValue}
   * @return created {@link BlockHash}
   */
  public static BlockHash of(final BytesValue bytesValue) {
    return new BlockHash(bytesValue);
  }

  /**
   * BlockHash constructor.
   *
   * @param encoded Base58 encoded value
   */
  public BlockHash(final Base58 encoded) {
    super(encoded);
  }

  /**
   * BlockHash constructor.
   *
   * @param bytesValue {@link BytesValue}
   */
  public BlockHash(final BytesValue bytesValue) {
    super(bytesValue);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> Optional<T> adapt(Class<T> adaptor) {
    if (adaptor.isAssignableFrom(BlockHash.class)) {
      return (Optional<T>) Optional.of(this);
    } else if (adaptor.isAssignableFrom(TxHash.class)) {
      return (Optional<T>) Optional.of(TxHash.of(getBytesValue()));
    } else if (adaptor.isAssignableFrom(ContractTxHash.class)) {
      return (Optional<T>) Optional.of(ContractTxHash.of(getBytesValue()));
    }
    return Optional.empty();
  }

}
