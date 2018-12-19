/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.tupleorerror.ResultOrErrorFuture;

@ApiAudience.Public
@ApiStability.Unstable
public interface AccountAsyncOperation {

  /**
   * Get account state by account.
   *
   * @param account account
   * @return future of an account state or error
   */
  ResultOrErrorFuture<AccountState> getState(Account account);

  /**
   * Get account state by address asynchronously.
   *
   * @param address account address
   * @return future of an account state or error
   */
  ResultOrErrorFuture<AccountState> getState(AccountAddress address);

  /**
   * Create name info of an account asynchronously.
   *
   * @param account an account
   * @param name an new name
   * @param nonce an nonce which is used in a transaction
   * @return a create name transaction hash or error
   */
  ResultOrErrorFuture<TxHash> createName(Account account, String name, long nonce);

  /**
   * Update name info of an account asynchronously.
   *
   * @param owner an name owner
   * @param name an already binded name
   * @param newOwner an new owner of name
   * @param nonce an nonce which is used in a transaction
   * @return a update name transaction hash or error
   */
  ResultOrErrorFuture<TxHash> updateName(Account owner, String name, AccountAddress newOwner,
      long nonce);

  /**
   * Get owner of an account name asynchronously.
   *
   * @param name an name of account
   * @return an account address binded with name or error
   */
  ResultOrErrorFuture<AccountAddress> getNameOwner(String name);

  /**
   * Sign for transaction asynchronously.
   *
   * @param account account to sign
   * @param rawTransaction raw transaction to sign
   * @return future of signed transaction or error
   */
  ResultOrErrorFuture<Transaction> sign(Account account, RawTransaction rawTransaction);

  /**
   * Verify transaction asynchronously.
   *
   * @param account account to verify
   * @param transaction transaction to verify
   * @return future of verify result or error
   */
  ResultOrErrorFuture<Boolean> verify(Account account, Transaction transaction);

}
