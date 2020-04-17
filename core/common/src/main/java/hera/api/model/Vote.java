/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@Builder(builderMethodName = "newBuilder")
@ApiAudience.Public
@ApiStability.Unstable
public class Vote implements GovernancePayload, Payload {

  @NonNull
  protected final String voteId;

  @Getter
  @NonNull
  protected final List<String> candidates;

  @Override
  public String getOperationName() {
    return "v1" + voteId;
  }

}