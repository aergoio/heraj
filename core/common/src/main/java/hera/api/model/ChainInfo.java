/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
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
public class ChainInfo {

  public static final ChainInfo EMPTY = ChainInfo.newBuilder().build();

  @NonNull
  @Default
  protected final ChainId chainId = ChainId.EMPTY;

  @Default
  protected final int blockProducerCount = 0;

  @Default
  protected final long maxBlockSize = 0L; // bytes

  @NonNull
  @Default
  protected final Aer totalTokenAmount = Aer.ZERO;

  @NonNull
  @Default
  protected final Aer minimumStakingAmount = Aer.ZERO;

  @NonNull
  @Default
  protected final Aer totalStaked = Aer.ZERO;

  @NonNull
  @Default
  protected final Aer gasPrice = Aer.ZERO;

  @NonNull
  @Default
  protected final Aer namingPrice = Aer.ZERO;

  @NonNull
  @Default
  protected final Aer totalVotingPower = Aer.ZERO;

  @NonNull
  @Default
  protected final Aer votingReward = Aer.ZERO;

}
