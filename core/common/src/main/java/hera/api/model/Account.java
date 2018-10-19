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
   * Bind state to an account. If nonce is less or equals to 0, nonce is set as 1. If balance is
   * less than 1, balance is set as 0. Remember, {@link AccountState#address} is not binded. Only
   * nonce and balance is binded.
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
   * Get nonce of an account.
   *
   * @return nonce
   */
  long getNonce();

  /**
   * Get nonce of an account and imcrement it.
   *
   * @return nonce
   */
  long getNonceAndIncrement();

  /**
   * Increment nonce by one.
   */
  void incrementNonce();

  /**
   * Set balanced for account. If a passed {@code balance} is less than 0, balance is set as 0.
   *
   * @param balance balance to set
   */
  void setBalance(long balance);

  /**
   * Get balance of an balance.
   *
   * @return balance
   */
  long getBalance();

}
