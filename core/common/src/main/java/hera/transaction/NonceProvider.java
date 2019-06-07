/*
 * @copyright defined in LICENSE.txt
 */

package hera.transaction;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;

@ApiAudience.Public
@ApiStability.Unstable
public interface NonceProvider {

  /**
   * Bind nonce of {@code accountState} with accountAddress of {@code accountState}.
   *
   * @param accountState an account state
   */
  void bindNonce(AccountState accountState);

  /**
   * Bind nonce to {@code accountAddress}. Next time nonce is required, it will be provided on base
   * of passed nonce.
   *
   * @param accountAddress an account address
   * @param nonce an nonce
   */
  void bindNonce(AccountAddress accountAddress, long nonce);

  /**
   * Get next nonce of {@code accountAddress} and increment it. If an nonce of
   * {@code accountAddress} is not binded yet, it will be treated as new account (nonce starts from
   * 0).
   *
   * @param accountAddress an accountAddress
   * @return an nonce
   */
  long incrementAndGetNonce(AccountAddress accountAddress);

}
