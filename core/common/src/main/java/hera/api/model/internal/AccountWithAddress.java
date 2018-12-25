/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model.internal;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.AccountAddress;
import hera.key.AergoKey;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ApiAudience.Private
@ApiStability.Unstable
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class AccountWithAddress extends AbstractAccount {

  @NonNull
  @Getter
  protected final AccountAddress address;

  @Override
  public AergoKey getKey() {
    return null;
  }

}
