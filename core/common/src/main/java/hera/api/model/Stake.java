/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import lombok.Getter;

public class Stake implements GovernancePayload, Payload {

  @Getter
  protected final String operationName = "v1stake";

}
