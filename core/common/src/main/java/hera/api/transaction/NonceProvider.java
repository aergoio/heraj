/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.transaction;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;

/**
 * Provides nonce of accounts.
 */
@ApiAudience.Public
@ApiStability.Unstable
public interface NonceProvider {

  /**
   * Bind nonce of {@code accountState} with accountAddress of {@code accountState}. The operation
   * must be thread-safe.
   *
   * @param accountState an account state
   */
  void bindNonce(AccountState accountState);

  /**
   * Bind nonce to {@code accountAddress}. Next time nonce is required, it will be provided on base
   * of passed nonce. The operation must be thread-safe.
   *
   * @param accountAddress an account address
   * @param nonce an nonce
   */
  void bindNonce(AccountAddress accountAddress, long nonce);

  /**
   * Increment nonce of {@code accountAddress} get it. If an nonce of {@code accountAddress} is not
   * binded yet, it will be treated as new account (nonce starts from 0). The operation must be
   * thread-safe.
   *
   * @param accountAddress an accountAddress
   * @return an nonce
   */
  long incrementAndGetNonce(AccountAddress accountAddress);

  /**
   * Get last used nonce of {@code accountAddress}. If an nonce of {@code accountAddress} is not
   * binded yet, it will be treated as new account (nonce starts from 0). The operation must be
   * thread-safe.
   *
   * @param accountAddress an accountAddress
   * @return an nonce
   */
  long getLastUsedNonce(AccountAddress accountAddress);

  /**
   * Clear cached nonce bound to {@code accountAddress}. Returning low nonce is generally safe, since the caller
   * can detect exception easily and then sync nonce and retry the committing tx.
   * The operation must be thread-safe.
   *
   * @param accountAddress an account address
   */
  void releaseNonce(AccountAddress accountAddress);

}
