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
public class AccountState {

  public static final AccountState EMPTY = AccountState.newBuilder().build();

  @NonNull
  @Default
  protected final AccountAddress address = AccountAddress.EMPTY;

  @Default
  protected final long nonce = 0L;

  @NonNull
  @Default
  protected final Aer balance = Aer.EMPTY;

}
