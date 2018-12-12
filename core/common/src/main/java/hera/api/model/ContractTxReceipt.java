/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class ContractTxReceipt {

  @Getter
  protected final ContractAddress contractAddress;

  @Getter
  protected final String status;

  @Getter
  protected final String ret;

  /**
   * ContractTxReceipt constructor.
   *
   * @param contractAddress a contract address
   * @param status a contract status
   * @param ret a return value
   */
  public ContractTxReceipt(final ContractAddress contractAddress, final String status,
      final String ret) {
    this.contractAddress =
        null != contractAddress ? contractAddress : new ContractAddress(BytesValue.EMPTY);
    this.status = status;
    this.ret = ret;
  }

}
