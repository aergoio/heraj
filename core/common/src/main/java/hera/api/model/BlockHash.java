/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.util.Base58Utils;
import java.io.IOException;
import java.util.Arrays;

public class BlockHash extends Hash {

  /**
   * Create {@code BlockHash} with a raw bytes array.
   *
   * @param bytes value
   * @return created {@link BlockHash}
   */
  public static BlockHash of(final byte[] bytes) {
    if (null == bytes) {
      return new BlockHash(null);
    }
    return new BlockHash(Arrays.copyOf(bytes, bytes.length));
  }

  /**
   * Create {@code BlockHash} with a base58 encoded value.
   *
   * @param encoded base58 encoded value
   * @return created {@link BlockHash}
   * @throws IOException when decoding error
   */
  public static BlockHash of(final String encoded) throws IOException {
    if (null == encoded) {
      return new BlockHash(null);
    }
    return of(Base58Utils.decode(encoded));
  }

  public BlockHash(final byte[] value) {
    super(value);
  }

}
