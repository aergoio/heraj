/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.Aer;
import hera.api.model.RawTransaction;
import hera.api.model.StakingInfo;
import hera.api.model.Transaction;
import hera.api.model.TxHash;

@ApiAudience.Public
@ApiStability.Unstable
public interface AccountOperation {

  /**
   * Get account state by account.
   *
   * @param account account
   * @return an account state
   */
  AccountState getState(Account account);

  /**
   * Get account state by address.
   *
   * @param address account address
   * @return an account state
   */
  AccountState getState(AccountAddress address);

  /**
   * Create name info of an account.
   *
   * @param account an account
   * @param name an new name
   * @param nonce an nonce which is used in a transaction
   * @return a create name transaction hash
   */
  TxHash createName(Account account, String name, long nonce);

  /**
   * Update name info of an account.
   *
   * @param owner an name owner
   * @param name an already binded name
   * @param newOwner an new owner of name
   * @param nonce an nonce which is used in a transaction
   * @return a update name transaction hash
   */
  TxHash updateName(Account owner, String name, AccountAddress newOwner, long nonce);

  /**
   * Get owner of an account name.
   *
   * @param name an name of account
   * @return an account address binded with name
   */
  AccountAddress getNameOwner(String name);

  /**
   * Staking an account with amount.
   *
   * @param account an account to stake
   * @param amount an amount to stake
   * @param nonce an nonce which is used in a transaction
   * @return a staking transaction hash
   */
  TxHash stake(Account account, Aer amount, long nonce);

  /**
   * Unstaking an account with amount.
   *
   * @param account an account to stake
   * @param amount an amount to stake
   * @param nonce an nonce which is used in a transaction
   * @return a staking transaction hash
   */
  TxHash unstake(Account account, Aer amount, long nonce);

  /**
   * Get staking information of {@code accountAddress}.
   *
   * @param accountAddress an account address to check staking information
   * @return a staking information
   */
  StakingInfo getStakingInfo(AccountAddress accountAddress);

  /**
   * Sign for transaction.
   *
   * @param account account to sign
   * @param rawTransaction raw transaction to sign
   * @return signed transaction
   */
  Transaction sign(Account account, RawTransaction rawTransaction);

  /**
   * Verify transaction.
   *
   * @param account account to verify
   * @param transaction transaction to verify
   * @return verify result
   */
  boolean verify(Account account, Transaction transaction);

}
