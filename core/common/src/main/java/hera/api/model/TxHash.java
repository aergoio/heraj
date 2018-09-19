/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

public class TxHash extends Hash {

  /**
   * Create {@code TxHash} with a raw bytes array.
   *
   * @param bytes value
   * @return created {@link TxHash}
   */
  public static TxHash of(final byte[] bytes) {
    return of(bytes, TxHash::new);
  }

  /**
   * Create {@code TxHash} with a base58 encoded value.
   *
   * @param encoded base58 encoded value
   * @return created {@link TxHash}
   */
  public static TxHash of(final String encoded) {
    return of(encoded, TxHash::new);
  }

  public TxHash(final byte[] value) {
    super(value);
  }

}
