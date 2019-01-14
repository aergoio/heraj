/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Account;
import hera.api.model.AccountState;
import hera.api.model.StakingInfo;
import hera.exception.UnbindedAccountException;

@ApiAudience.Private
@ApiStability.Unstable
public interface NonceManagable {

  /**
   * Get a current account.
   *
   * @return an account
   * @throws UnbindedAccountException if account isn't binded
   */
  Account getCurrentAccount();

  /**
   * Get state of current account.
   *
   * @return a state of current account
   * @throws UnbindedAccountException if account isn't binded
   */
  AccountState getCurrentAccountState();

  /**
   * Get staking information of current account.
   *
   * @return a staking information of current account
   * @throws UnbindedAccountException if account isn't binded
   */
  StakingInfo getCurrentAccountStakingInfo();

  /**
   * Get recently used nonce value.
   *
   * @return a recently used nonce
   * @throws UnbindedAccountException if account isn't binded
   */
  long getRecentlyUsedNonce();

  /**
   * Increment an nonce and get it.
   *
   * @return an incremented nonce
   * @throws UnbindedAccountException if account isn't binded
   */
  long incrementAndGetNonce();

}
