/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import lombok.Getter;
import lombok.Setter;

public class AccountState {

  @Getter
  @Setter
  protected AccountAddress address;

  @Getter
  @Setter
  protected long nonce;

  @Getter
  @Setter
  protected long balance;

}
