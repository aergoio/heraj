/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

public class BlockHash extends Hash {

  /**
   * Factory method.
   *
   * @param bytes value
   * @return created {@link BlockHash}
   */
  public static BlockHash of(final byte[] bytes) {
    return new BlockHash(bytes);
  }

  public BlockHash(final byte[] value) {
    super(value);
  }

}
