/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ApiAudience.Public
@ApiStability.Unstable
@ToString
@EqualsAndHashCode
public class VoteInfo {

  @Getter
  protected final String voteId;

  @Getter
  protected final List<String> candidateIds;

  /**
   * VoteInfo constructor.
   * 
   * @param voteId a vote id
   * @param candidates a candidate
   */
  public VoteInfo(final String voteId, final List<String> candidates) {
    assertNotNull(voteId, "Vote id must not null");
    assertNotNull(candidates, "Candidate must not null");
    this.voteId = voteId;
    this.candidateIds = candidates;
  }

}
