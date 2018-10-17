/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class AccountState {

  @Getter
  @Setter
  protected AccountAddress address = new AccountAddress(BytesValue.EMPTY);

  @Getter
  @Setter
  protected long nonce;

  @Getter
  @Setter
  protected long balance;

}
