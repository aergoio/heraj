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
public class StakeInfo {

  public static final StakeInfo EMPTY = StakeInfo.newBuilder().build();

  @NonNull
  @Default
  protected final AccountAddress address = AccountAddress.EMPTY;

  @NonNull
  @Default
  protected final Aer amount = Aer.ZERO;

  @Default
  protected final long blockNumber = 0L;

}
