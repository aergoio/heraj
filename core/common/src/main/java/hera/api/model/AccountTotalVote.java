/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;
import static java.util.Collections.unmodifiableList;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
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
public class AccountTotalVote {

  public static final AccountTotalVote EMPTY = AccountTotalVote.newBuilder().build();

  @NonNull
  @Default
  protected final StakeInfo stakeInfo = StakeInfo.EMPTY;

  @NonNull
  @Default
  protected final List<VoteInfo> voteInfos = unmodifiableList(Collections.<VoteInfo>emptyList());

  AccountTotalVote(final StakeInfo stakeInfo, final List<VoteInfo> voteInfos) {
    assertNotNull(stakeInfo, "Staked amount must not null");
    assertNotNull(voteInfos, "Vote infos must not null");
    this.stakeInfo = stakeInfo;
    this.voteInfos = unmodifiableList(voteInfos);
  }

}
