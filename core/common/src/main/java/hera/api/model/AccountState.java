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
public class AccountState {

  @NonNull
  @Default
  AccountAddress address = AccountAddress.of(BytesValue.EMPTY);

  long nonce;

  @NonNull
  @Default
  Aer balance = Aer.EMPTY;

}
