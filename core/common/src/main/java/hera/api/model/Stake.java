/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import lombok.Getter;

@ApiAudience.Public
@ApiStability.Unstable
public class Stake implements GovernancePayload, Payload {

  @Getter
  protected final String operationName = "v1stake";

}
