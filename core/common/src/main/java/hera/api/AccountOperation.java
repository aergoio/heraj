/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.tupleorerror.ResultOrError;
import java.util.List;

public interface AccountOperation {

  /**
   * Get account list.
   *
   * @return account list or error
   */
  ResultOrError<List<Account>> list();

  /**
   * Create an account with password.
   *
   * @param password account password
   * @return created account or error
   */
  ResultOrError<Account> create(String password);

  /**
   * Get account by address.
   *
   * @param address account address
   * @return an account or error
   */
  ResultOrError<Account> get(AccountAddress address);

  /**
   * Lock an account.
   *
   * @param address account address
   * @param password account password
   * @return lock result or error
   */
  ResultOrError<Boolean> lock(AccountAddress address, String password);

  /**
   * Unlock an account.
   *
   * @param address account address
   * @param password account password
   * @return unlock result or error
   */
  ResultOrError<Boolean> unlock(AccountAddress address, String password);

  /**
   * Get account state by account address.
   *
   * @param address account address
   * @return an account state or error
   */
  ResultOrError<AccountState> getState(AccountAddress address);
}
