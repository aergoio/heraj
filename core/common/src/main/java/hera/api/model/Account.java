/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.key.AergoKey;
import hera.util.Adaptor;

public interface Account extends Adaptor {

  /**
   * Get address of an account.
   *
   * @return address
   */
  AccountAddress getAddress();

  /**
   * Bind state to an account. If nonce is less or equals to 0, nonce is set as 1. Remember,
   * {@link AccountState#address} is not binded. Only nonce is binded.
   *
   * @param state state to bind
   */
  void bindState(AccountState state);

  /**
   * Set nonce for account. If a passed {@code nonce} is less or equals to 0, nonce is set as 1.
   *
   * @param nonce nonce to set
   */
  void setNonce(long nonce);

  /**
   * Get nonce for account.
   *
   * @return an nonce
   */
  long getNonce();


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
