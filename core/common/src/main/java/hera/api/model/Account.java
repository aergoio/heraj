/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class Account {

  @Getter
  @Setter
  protected AccountAddress address = new AccountAddress(null);

  @Getter
  @Setter
  protected String password;

  @Getter
  @Setter
  protected long nonce;

  @Getter
  @Setter
  protected long balance;

  /**
   * Create {@link Account}.
   *
   * @param address address of account in byte array
   * @param password password of account
   *
   * @return created account
   */
  public static Account of(final byte[] address, final String password) {
    return of(AccountAddress.of(address), password);
  }

  /**
   * Create {@link Account}.
   *
   * @param address address of account
   * @param password password of account
   *
   * @return created account
   */
  public static Account of(AccountAddress address, final String password) {
    final Account account = new Account();
    account.setAddress(address);
    account.setPassword(password);
    return account;
  }

}
