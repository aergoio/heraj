/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

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
   * Get next nonce of an account.
   *
   * @return nonce
   */
  long nextNonce();

  /**
   * Increment nonce by one.
   */
  void incrementNonce();

}
