/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class ContractTxReceipt {

  @Getter
  @Setter
  protected ContractAddress contractAddress = new ContractAddress(BytesValue.EMPTY);

  @Getter
  @Setter
  protected String status;

  @Getter
  @Setter
  protected String ret;
}
