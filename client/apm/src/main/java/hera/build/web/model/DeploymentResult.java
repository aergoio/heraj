/*
 * @copyright defined in LICENSE.txt
 */

package hera.build.web.model;

import hera.api.model.ContractInferface;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class DeploymentResult {
  @Getter
  @Setter
  protected String buildUuid;

  @Getter
  @Setter
  protected String contractTxHash;

  @Getter
  @Setter
  protected ContractInferface contractInterface;
}
