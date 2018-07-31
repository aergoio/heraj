/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

public class Hash extends BytesValue {

  public Hash(final byte[] value) {
    super(value);
  }

  public byte[] getBytesValue() {
    return value;
  }
}
