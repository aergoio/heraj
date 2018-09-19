/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

public class BlockHash extends Hash {

  /**
   * Create {@code BlockHash} with a raw bytes array.
   *
   * @param bytes value
   * @return created {@link BlockHash}
   */
  public static BlockHash of(final byte[] bytes) {
    return of(bytes, BlockHash::new);
  }

  /**
   * Create {@code BlockHash} with a base58 encoded value.
   *
   * @param encoded base58 encoded value
   * @return created {@link BlockHash}
   */
  public static BlockHash of(final String encoded) {
    return of(encoded, BlockHash::new);
  }

  public BlockHash(final byte[] value) {
    super(value);
  }

}
