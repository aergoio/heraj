/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.util.Base58Utils;
import java.io.IOException;
import java.util.Arrays;

public class TxHash extends Hash {

  /**
   * Create {@code TxHash} with a raw bytes array.
   *
   * @param bytes value
   * @return created {@link TxHash}
   */
  public static TxHash of(final byte[] bytes) {
    if (null == bytes) {
      return new TxHash(null);
    }
    return new TxHash(Arrays.copyOf(bytes, bytes.length));
  }

  /**
   * Create {@code TxHash} with a base58 encoded value.
   *
   * @param encoded base58 encoded value
   * @return created {@link TxHash}
   * @throws IOException when decoding error
   */
  public static TxHash of(final String encoded) throws IOException {
    if (null == encoded) {
      return new TxHash(null);
    }
    return of(Base58Utils.decode(encoded));
  }

  public TxHash(final byte[] value) {
    super(value);
  }

}
