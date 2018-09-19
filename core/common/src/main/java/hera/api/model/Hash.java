/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.util.Base58Utils;
import java.io.IOException;
import java.util.Arrays;

public class Hash extends BytesValue {

  /**
   * Create {@code Hash} with a raw bytes array.
   *
   * @param bytes value
   * @return created {@link Hash}
   */
  public static Hash of(final byte[] bytes) {
    if (null == bytes) {
      return new Hash(null);
    }
    return new Hash(Arrays.copyOf(bytes, bytes.length));
  }

  /**
   * Create {@code Hash} with a base58 encoded value.
   *
   * @param encoded base58 encoded value
   * @return created {@link Hash}
   * @throws IOException when decoding error
   */
  public static Hash of(final String encoded) throws IOException {
    if (null == encoded) {
      return new Hash(null);
    }
    return of(Base58Utils.decode(encoded));
  }

  public Hash(final byte[] value) {
    super(value);
  }

  public byte[] getBytesValue() {
    return value;
  }
}
