/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import java.util.List;
import java.util.Optional;

public interface AccountOperation {

  /**
   * Get account list.
   *
   * @return account list
   */
  List<Account> list();

  /**
   * Create an account with password.
   *
   * @param password account password
   * @return created account if success, otherwise null
   */
  Account create(String password);

  /**
   * Get account by address.
   *
   * @param address account address
   * @return an Optional account if an account with address is present, otherwise an empty Optional
   */
  Optional<Account> get(AccountAddress address);

  /**
   * Lock an account.
   *
   * @param domainAccount account
   * @return whether account is locked or not
   */
  boolean lock(Account domainAccount);

  /**
   * Unlock an account.
   *
   * @param domainAccount account
   * @return whether account is unlocked or not
   */
  boolean unlock(Account domainAccount);

  /**
   * Get account state by account address.
   *
   * @param address account address
   * @return an Optional account state if an account with address is present, otherwise an empty
   *         Optional
   */
  Optional<AccountState> getState(AccountAddress address);
}
