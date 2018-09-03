/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

public class TxHash extends Hash {

  /**
   * Factory method.
   *
   * @param bytes value
   * @return created {@link TxHash}
   */
  public static TxHash of(final byte[] bytes) {
    return new TxHash(bytes);
  }

  public TxHash(final byte[] value) {
    super(value);
  }

}
