/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;
import static java.util.Collections.unmodifiableList;

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
public class AccountTotalVote {

  @Getter
  protected final StakeInfo stakeInfo;

  @Getter
  protected final List<VoteInfo> voteInfos;

  /**
   * VoteTotal constructor.
   *
   * @param stakeInfo a stake information
   * @param voteInfos a vote informations
   */
  public AccountTotalVote(final StakeInfo stakeInfo, final List<VoteInfo> voteInfos) {
    assertNotNull(stakeInfo, "Staked amount must not null");
    assertNotNull(voteInfos, "Vote infos must not null");
    this.stakeInfo = stakeInfo;
    this.voteInfos = unmodifiableList(voteInfos);
  }

}
