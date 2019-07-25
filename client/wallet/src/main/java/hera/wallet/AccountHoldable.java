/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import hera.api.model.Account;
import hera.api.model.AccountState;
import hera.api.model.AccountTotalVote;
import hera.api.model.StakeInfo;
import hera.exception.WalletConnectionException;
import hera.exception.WalletRpcException;

public interface AccountHoldable {

  /**
   * Get a current account.
   *
   * @return an account
   */
  Account getAccount();

  /**
   * Get state of current account.
   *
   * @return a state of current account
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  AccountState getAccountState();

  /**
   * Get staking information of current account.
   *
   * @return a staking information of current account
   *
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  StakeInfo getStakingInfo();

  /**
   * Get votes which current account votes for.
   *
   * @return votes list
   *
   * @throws WalletConnectionException on connection failure
   * @throws WalletRpcException on rpc error
   */
  AccountTotalVote getVotes();

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
