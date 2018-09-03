/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class Authentication {

  public static Authentication of(final AccountAddress address, final String password) {
    return new Authentication(address, password);
  }

  @Getter
  @Setter
  protected AccountAddress address = new AccountAddress(null);

  @Getter
  @Setter
  protected String password;
}
