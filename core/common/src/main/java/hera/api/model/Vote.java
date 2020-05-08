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

@ApiAudience.Public
@ApiStability.Unstable
@Getter
@ToString
@EqualsAndHashCode
@Builder(builderMethodName = "newBuilder")
public class Vote implements GovernancePayload, Payload {

  @NonNull
  protected final String voteId;

  @NonNull
  protected final List<String> candidates;

  @Override
  public String getOperationName() {
    return "v1" + voteId;
  }

}
