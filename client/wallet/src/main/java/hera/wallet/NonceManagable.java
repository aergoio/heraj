/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import hera.api.model.Account;
import hera.api.model.AccountState;

public interface NonceManagable extends LookupClient {

  /**
   * Get a current account.
   *
   * @return an account
   */
  Account getCurrentAccount();

  /**
   * Get state of current account.
   *
   * @return a state of current account
   */
  AccountState getCurrentAccountState();

  /**
   * Get recently used nonce value.
   *
   * @return a recently used nonce
   */
  long getRecentlyUsedNonce();

  /**
   * Increment an nonce and get it.
   *
   * @return an incremented nonce
   */
  long incrementAndGetNonce();

}
