/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Account;
import hera.api.model.AccountState;
import hera.api.model.StakingInfo;
import hera.api.model.VotingInfo;
import hera.exception.UnbindedAccountException;
import java.util.List;

@ApiAudience.Public
@ApiStability.Unstable
public interface AccountHoldable {

  /**
   * Get a current account.
   *
   * @return an account
   * @throws UnbindedAccountException if account isn't binded
   */
  Account getAccount();

  /**
   * Get a current account.
   *
   * @return an account
   * @deprecated use {@link #getAccount()} instead.
   */
  @Deprecated
  Account getCurrentAccount();

  /**
   * Get state of current account.
   *
   * @return a state of current account
   * @throws UnbindedAccountException if account isn't binded
   */
  AccountState getAccountState();

  /**
   * Get state of current account.
   *
   * @return a state of current account
   * @deprecated use {@link #getAccountState()} instead.
   */
  @Deprecated
  AccountState getCurrentAccountState();

  /**
   * Get staking information of current account.
   *
   * @return a staking information of current account
   * @throws UnbindedAccountException if account isn't binded
   */
  StakingInfo getStakingInfo();

  /**
   * Get votes which current account votes for.
   *
   * @return votes list
   */
  List<VotingInfo> listVotes();

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
