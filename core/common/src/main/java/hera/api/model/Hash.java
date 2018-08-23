/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

public class Hash extends BytesValue {

  /**
   * Factory method.
   *
   * @param bytes value
   * @return created {@link Hash}
   */
  public static Hash of(final byte[] bytes) {
    return new Hash(bytes);
  }

  public Hash(final byte[] value) {
    super(value);
  }

  public byte[] getBytesValue() {
    return value;
  }
}
