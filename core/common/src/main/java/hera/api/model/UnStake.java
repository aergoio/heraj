/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import lombok.Getter;

public class UnStake implements GovernancePayload, Payload {

  @Getter
  protected final String operationName = "v1unstake";

}
