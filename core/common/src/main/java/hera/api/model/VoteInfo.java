/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;
import static java.util.Collections.unmodifiableList;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.util.StringUtils;
import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.Builder.Default;
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
public class VoteInfo {

  @NonNull
  @Default
  protected final String voteId = StringUtils.EMPTY_STRING;

  @NonNull
  @Default
  protected final List<String> candidateIds = unmodifiableList(Collections.<String>emptyList());

  VoteInfo(final String voteId, final List<String> candidates) {
    assertNotNull(voteId, "Vote id must not null");
    assertNotNull(candidates, "Candidate must not null");
    this.voteId = voteId;
    this.candidateIds = unmodifiableList(candidates);
  }

}
