/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.AccountTotalVote;
import hera.api.model.Aer;
import hera.api.model.ElectedCandidate;
import hera.api.model.RawTransaction;
import hera.api.model.StakeInfo;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.key.AergoSignVerifier;
import hera.key.Signer;
import java.util.List;

/**
 * Provide account related operations. It provides followings:
 *
 * <ul>
 * <li>lookup account state</li>
 * <li>naming related operations</li>
 * <li>staking related operations</li>
 * <li>voting related operations</li>
 * </ul>
 *
 * @author bylee, Taeik Lim
 *
 */
@ApiAudience.Public
@ApiStability.Unstable
public interface AccountOperation {

  /**
   * Use {@link AccountOperation#getState(AccountAddress)} instead.
   *
   * @param account an account
   * @return an account state
   */
  @Deprecated
  AccountState getState(Account account);

  /**
   * Get account state by address.
   *
   * @param address an account address
   * @return an account state
   */
  AccountState getState(AccountAddress address);

  /**
   * Use {@link #createName(Signer, String, long)} instead.
   *
   * @param account an account
   * @param name an new name
   * @param nonce an nonce which is used in a transaction
   * @return a create name transaction hash
   */
  @Deprecated
  TxHash createName(Account account, String name, long nonce);

  /**
   * Create name info of an account. Created name will be owned by {@code signer}.
   *
   * @param signer a signer
   * @param name an new name
   * @param nonce an nonce which is used in a transaction
   * @return a create name transaction hash
   */
  TxHash createName(Signer signer, String name, long nonce);

  /**
   * Use {@link #updateName(Signer, String, AccountAddress, long)} instead.
   *
   * @param owner an account
   * @param name an already binded name
   * @param newOwner an new owner of name
   * @param nonce an nonce which is used in a transaction
   * @return a update name transaction hash
   */
  @Deprecated
  TxHash updateName(Account owner, String name, AccountAddress newOwner, long nonce);

  /**
   * Update name info of an account. An signer must be owner of {@code name}.
   *
   * @param signer a signer
   * @param name an already binded name
   * @param newOwner an new owner of name
   * @param nonce an nonce which is used in a transaction
   * @return a update name transaction hash
   */
  TxHash updateName(Signer signer, String name, AccountAddress newOwner, long nonce);

  /**
   * Get owner of an account name.
   *
   * @param name an name of account
   * @return an account address binded with name. null if it has no owner
   */
  AccountAddress getNameOwner(String name);

  /**
   * Get owner of an account name at block {@code blockNumber}.
   *
   * @param name an name of account
   * @param blockNumber a block number
   * @return an account address binded with name. null if it has no owner
   */
  AccountAddress getNameOwner(String name, long blockNumber);

  /**
   * Use {@link #stake(Signer, Aer, long)} instead.
   *
   * @param account an account
   * @param amount an amount to stake
   * @param nonce an nonce which is used in a transaction
   * @return a staking transaction hash
   */
  @Deprecated
  TxHash stake(Account account, Aer amount, long nonce);

  /**
   * Staking an account with amount. An {@code amount} will be staked by {@code signer}.
   *
   * @param signer a signer
   * @param amount an amount to stake
   * @param nonce an nonce which is used in a transaction
   * @return a staking transaction hash
   */
  TxHash stake(Signer signer, Aer amount, long nonce);

  /**
   * Use {@link #unstake(Signer, Aer, long)} instead.
   *
   * @param account an account
   * @param amount an amount to stake
   * @param nonce an nonce which is used in a transaction
   * @return a staking transaction hash
   */
  @Deprecated
  TxHash unstake(Account account, Aer amount, long nonce);

  /**
   * Unstaking an account with amount.
   *
   * @param signer a signer
   * @param amount an amount to stake
   * @param nonce an nonce which is used in a transaction
   * @return a staking transaction hash
   */
  TxHash unstake(Signer signer, Aer amount, long nonce);

  /**
   * Get staking information of {@code accountAddress}.
   *
   * @param accountAddress an account address to check staking information
   * @return a staking information
   */
  StakeInfo getStakingInfo(AccountAddress accountAddress);

  /**
   * Vote to {@code candidates} with corresponding {@code voteId}. Pre-defined vote id is
   * followings:
   *
   *
   * <ul>
   * <li>"voteBP"</li>
   * </ul>
   * A {@code signer} must have staked aergo.
   *
   * @param signer a signer
   * @param voteId a vote id
   * @param candidates a candidates
   * @param nonce an nonce which is used in a transaction
   *
   * @return voting transaction hash
   */
  TxHash vote(Signer signer, String voteId, List<String> candidates, long nonce);

  /**
   * Get votes which {@code accountAddress} votes for.
   *
   * @param accountAddress an account address
   * @return voting info
   */
  AccountTotalVote getVotesOf(AccountAddress accountAddress);

  /**
   * Get elected candidates per {@code voteId} for current round.
   *
   * @param voteId a vote id
   * @param showCount a show count
   * @return a vote total
   */
  List<ElectedCandidate> listElected(String voteId, int showCount);

  /**
   * Use {@link Signer#sign(RawTransaction)} instead.
   *
   * @param account an account to sign
   * @param rawTransaction a raw transaction to sign
   * @return signed transaction
   */
  @Deprecated
  Transaction sign(Account account, RawTransaction rawTransaction);

  /**
   * Use {@link AergoSignVerifier#verify(Transaction)} instead.
   *
   * @param account an account to verify
   * @param transaction a signed transaction to verify
   * @return verification result
   */
  @Deprecated
  boolean verify(Account account, Transaction transaction);

}
