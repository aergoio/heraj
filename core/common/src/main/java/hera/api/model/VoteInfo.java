/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.util.StringUtils;
import java.util.List;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.NonNull;
import lombok.Value;

@ApiAudience.Public
@ApiStability.Unstable
@Value
@Builder(builderMethodName = "newBuilder")
public class VoteInfo {

  @NonNull
  @Default
  String voteId = StringUtils.EMPTY_STRING;

  @NonNull
  @Default
  List<String> candidateIds = emptyList();

  VoteInfo(final String voteId, final List<String> candidates) {
    assertNotNull(voteId, "Vote id must not null");
    assertNotNull(candidates, "Candidate must not null");
    this.voteId = voteId;
    this.candidateIds = unmodifiableList(candidates);
  }

}
