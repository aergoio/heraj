/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

public class Hash extends BytesValue {

  /**
   * Create {@code Hash} with a raw bytes array.
   *
   * @param bytes value
   * @return created {@link Hash}
   */
  public static Hash of(final byte[] bytes) {
    return of(bytes, Hash::new);
  }

  /**
   * Create {@code Hash} with a base58 encoded value.
   *
   * @param encoded base58 encoded value
   * @return created {@link Hash}
   */
  public static Hash of(final String encoded) {
    return of(encoded, Hash::new);
  }

  public Hash(final byte[] value) {
    super(value);
  }

  public byte[] getBytesValue() {
    return value;
  }
}
