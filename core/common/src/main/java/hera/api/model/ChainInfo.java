/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.NonNull;
import lombok.Value;

@ApiAudience.Public
@ApiStability.Unstable
@Value
@Builder(builderMethodName = "newBuilder")
public class ChainInfo {

  @NonNull
  @Default
  ChainId chainId = ChainId.newBuilder().build();

  int blockProducerCount;

  long maxBlockSize; // bytes

  @NonNull
  @Default
  Aer totalTokenAmount = Aer.EMPTY;

  @NonNull
  @Default
  Aer minimumStakingAmount = Aer.EMPTY;

  @NonNull
  @Default
  Aer totalStaked = Aer.EMPTY;

  @NonNull
  @Default
  Aer gasPrice = Aer.EMPTY;

  @NonNull
  @Default
  Aer namingPrice = Aer.EMPTY;

}
