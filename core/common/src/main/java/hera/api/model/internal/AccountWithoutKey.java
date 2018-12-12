/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model.internal;

import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.key.AergoKey;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class AccountWithoutKey extends AbstractAccount {

  @Setter
  @Getter
  protected AccountAddress address = new AccountAddress(BytesValue.EMPTY);

  @Override
  public AergoKey getKey() {
    return null;
  }

}
