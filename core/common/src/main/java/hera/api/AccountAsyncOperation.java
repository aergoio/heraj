/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.tupleorerror.ResultOrErrorFuture;
import java.util.List;
import java.util.Optional;

public interface AccountAsyncOperation {

  /**
   * Get account list asynchronously.
   *
   * @return future of account list or error
   */
  ResultOrErrorFuture<List<Account>> list();

  /**
   * Create an account with password asynchronously.
   *
   * @param password account password
   * @return future of created account or error
   */
  ResultOrErrorFuture<Account> create(String password);

  /**
   * Get account by address asynchronously.
   *
   * @param address account address
   * @return future of an Optional account or error
   */
  ResultOrErrorFuture<Optional<Account>> get(AccountAddress address);

  /**
   * Lock an account asynchronously.
   *
   * @param address account address
   * @param password account password
   * @return future of lock result or error
   */
  ResultOrErrorFuture<Boolean> lock(AccountAddress address, String password);

  /**
   * Unlock an account asynchronously.
   *
   * @param address account address
   * @param password account password
   * @return future of unlock result or error
   */
  ResultOrErrorFuture<Boolean> unlock(AccountAddress address, String password);

  /**
   * Get account state by account address asynchronously.
   *
   * @param address account address
   * @return future of an Optional account state or error
   */
  ResultOrErrorFuture<Optional<AccountState>> getState(AccountAddress address);
}
