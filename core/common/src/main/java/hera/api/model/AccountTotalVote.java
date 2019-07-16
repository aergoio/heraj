/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import java.util.List;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.NonNull;
import lombok.Value;

@ApiAudience.Public
@ApiStability.Unstable
@Value
@Builder(builderMethodName = "newBuilder")
public class AccountTotalVote {

  @NonNull
  @Default
  StakeInfo stakeInfo = StakeInfo.newBuilder().build();

  @NonNull
  @Default
  List<VoteInfo> voteInfos = emptyList();

  AccountTotalVote(final StakeInfo stakeInfo, final List<VoteInfo> voteInfos) {
    assertNotNull(stakeInfo, "Staked amount must not null");
    assertNotNull(voteInfos, "Vote infos must not null");
    this.stakeInfo = stakeInfo;
    this.voteInfos = unmodifiableList(voteInfos);
  }

}
