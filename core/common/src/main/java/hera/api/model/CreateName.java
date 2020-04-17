/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@ApiAudience.Public
@ApiStability.Unstable
@ToString
@EqualsAndHashCode
@Builder(builderMethodName = "newBuilder")
public class CreateName implements GovernancePayload, Payload {

  @Getter
  protected final String operationName = "v1createName";

  @Getter
  @NonNull
  protected final Name name;

}