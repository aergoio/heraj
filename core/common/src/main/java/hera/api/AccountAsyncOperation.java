/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface AccountAsyncOperation {

  /**
   * Get account list asynchronously.
   *
   * @return account list
   */
  CompletableFuture<List<Account>> list();

  /**
   * Create an account with password asynchronously.
   *
   * @param password account password
   * @return created account if success, otherwise null
   */
  CompletableFuture<Account> create(String password);

  /**
   * Get account by address asynchronously.
   *
   * @param address account address
   * @return an Optional account if an account with address is present, otherwise an empty Optional
   */
  CompletableFuture<Optional<Account>> get(AccountAddress address);

  /**
   * Lock an account asynchronously.
   *
   * @param domainAccount account
   * @return whether account is locked or not
   */
  CompletableFuture<Boolean> lock(Account domainAccount);

  /**
   * Unlock an account asynchronously.
   *
   * @param domainAccount account
   * @return whether account is unlocked or not
   */
  CompletableFuture<Boolean> unlock(Account domainAccount);

  /**
   * Get account state by account address asynchronously.
   *
   * @param address account address
   * @return an Optional account state if an account with address is present, otherwise an empty
   *         Optional
   */
  CompletableFuture<Optional<AccountState>> getState(AccountAddress address);
}
