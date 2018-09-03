/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

public class ContractTxHash extends Hash {

  /**
   * Factory method.
   *
   * @param bytes value
   * @return created {@link ContractTxHash}
   */
  public static ContractTxHash of(final byte[] bytes) {
    return new ContractTxHash(bytes);
  }

  public ContractTxHash(final byte[] value) {
    super(value);
  }

}
