/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Authentication;
import hera.client.AergoClient;
import hera.key.Signer;

/**
 * A wallet api holding single identity. It interact with {@link hera.keystore.KeyStore}. Has a
 * signing role.
 *
 * @author taeiklim
 */
@ApiAudience.Public
@ApiStability.Unstable
public interface WalletApi extends Signer {

  /**
   * Bind an aergo client to use.
   *
   * @param aergoClient an aergo client
   * @deprecated use {@link #with(AergoClient)} instead.
   */
  @Deprecated
  void bind(AergoClient aergoClient);

  /**
   * Prepare aergo client to use.
   *
   * @param aergoClient an aergo client to use
   * @return WalletApiWithClient
   */
  PreparedWalletApi with(AergoClient aergoClient);

  /**
   * Unlock an account and bind it to wallet api.
   *
   * @param authentication an authentication to unlock account
   * @return an unlock result
   */
  boolean unlock(Authentication authentication);

  /**
   * Lock an account.
   *
   * @param authentication an authentication to lock account binded to wallet api
   * @return a lock result
   * @deprecated use {@link #lock()} instead.
   */
  @Deprecated
  boolean lock(Authentication authentication);

  /**
   * Lock an account.
   *
   * @return a lock result
   */
  boolean lock();

  /**
   * Get transaction api.
   *
   * @return a transaction api
   * @deprecated use {@link #with(AergoClient)} instead.
   */
  @Deprecated
  TransactionApi transactionApi();

  /**
   * Get query api.
   *
   * @return a query api
   * @deprecated use {@link #with(AergoClient)} instead.
   */
  @Deprecated
  QueryApi queryApi();

}
