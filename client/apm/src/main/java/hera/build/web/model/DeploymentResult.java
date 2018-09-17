/*
 * @copyright defined in LICENSE.txt
 */

package hera.build.web.model;

import hera.api.model.ContractInferface;
import lombok.Getter;
import lombok.Setter;

public class DeploymentResult {
  @Getter
  @Setter
  protected String buildUuid;

  @Getter
  @Setter
  protected String encodedContractTransactionHash;

  @Getter
  @Setter
  protected ContractInferface contractInterface;

  public String toString() {
    return "Transaction[" + getEncodedContractTransactionHash() + "]";
  }
}
