/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.key.AergoKey;

@Deprecated
public interface Account {

  /**
   * Get address of an account.
   *
   * @return address
   */
  AccountAddress getAddress();

  /**
   * Bind state to an account. If nonce is less or equals to 0, nonce is set as 1. Remember, address
   * is not binded. Only nonce is binded.
   *
   * @param state state to bind
   */
  void bindState(AccountState state);

  /**
   * Set nonce for account. If a passed {@code nonce} is less than 0, set as 0.
   *
   * @param nonce nonce to set
   */
  void setNonce(long nonce);

  /**
   * Get nonce for account.
   *
   * @return an nonce
   */
  long getRecentlyUsedNonce();

  /**
   * Increment an nonce and get it.
   *
   * @return an nonce
   */
  long incrementAndGetNonce();

  /**
   * Get {@code AergoKey} if holds. Otherwise return null.
   *
   * @return {@code AergoKey} if holds. Otherwise null.
   */
  AergoKey getKey();

}
