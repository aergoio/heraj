/*
 * @copyright defined in LICENSE.txt
 */

package hera.build.web.model;

import lombok.Getter;
import lombok.Setter;

public class DeploymentResult {
  @Getter
  @Setter
  protected String buildUuid;

  @Getter
  @Setter
  protected String contractAddress;
}
