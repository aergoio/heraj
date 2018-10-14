/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.util.Adaptor;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class Account implements Adaptor {

  @Getter
  @Setter
  protected AccountAddress address = new AccountAddress(BytesValue.EMPTY);

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
   * @param address address of account
   * @param password password of account
   *
   * @return created account
   */
  public static Account of(final AccountAddress address, final String password) {
    final Account account = new Account();
    account.setAddress(address);
    account.setPassword(password);
    return account;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> Optional<T> adapt(Class<T> adaptor) {
    if (adaptor.isAssignableFrom(Account.class)) {
      return (Optional<T>) Optional.of(this);
    } else if (adaptor.isAssignableFrom(AccountAddress.class)) {
      return (Optional<T>) Optional.of(getAddress());
    }
    return Optional.empty();
  }

}
