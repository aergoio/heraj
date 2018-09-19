/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

public class ContractTxHash extends Hash {

  /**
   * Create {@code ContractTxHash} with a raw bytes array.
   *
   * @param bytes value
   * @return created {@link ContractTxHash}
   */
  public static ContractTxHash of(final byte[] bytes) {
    return of(bytes, ContractTxHash::new);
  }

  /**
   * Create {@code ContractTxHash} with a base58 encoded value.
   *
   * @param encoded base58 encoded value
   * @return created {@link ContractTxHash}
   */
  public static ContractTxHash of(final String encoded) {
    return of(encoded, ContractTxHash::new);
  }

  public ContractTxHash(final byte[] value) {
    super(value);
  }

}
