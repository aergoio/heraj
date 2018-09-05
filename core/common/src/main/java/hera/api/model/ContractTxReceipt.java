/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class ContractTxReceipt {

  @Getter
  @Setter
  protected ContractAddress contractAddress = new ContractAddress(null);

  @Getter
  @Setter
  protected String status;

  @Getter
  @Setter
  protected String ret;
}
