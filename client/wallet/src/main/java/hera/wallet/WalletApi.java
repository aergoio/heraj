/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.client.AergoClient;
import hera.exception.WalletException;
import hera.key.Signer;

/**
 * A wallet api holding single identity. It interact with {@link hera.keystore.KeyStore}. Has a
 * signing role.
 *
 * @author taeiklim
 *
 */
@ApiAudience.Public
@ApiStability.Unstable
public interface WalletApi extends Signer {

  /**
   * Get an principal of current wallet. null if not binded.
   *
   * @return an principal of current wallet. null if not binded
   */
  AccountAddress getPrincipal();

  /**
   * Bind an aergo client to use.
   *
   * @param aergoClient an aergo client
   */
  void bind(AergoClient aergoClient);

  /**
   * Unlock an account and bind it to wallet api.
   *
   * @param authentication an authentication to unlock account
   * @return an unlock result
   *
   * @throws WalletException on wallet error
   */
  boolean unlock(Authentication authentication);

  /**
   * Lock an account.
   *
   * @param authentication an authentication to lock account binded to wallet api
   * @return a lock result
   *
   * @throws WalletException on wallet error
   */
  boolean lock(Authentication authentication);

  /**
   * Get transaction api.
   *
   * @return a transaction api
   *
   * @throws WalletException on wallet error
   */
  TransactionApi transactionApi();

  /**
   * Get query api.
   *
   * @return a query api
   *
   * @throws WalletException on wallet error
   */
  QueryApi queryApi();

}
