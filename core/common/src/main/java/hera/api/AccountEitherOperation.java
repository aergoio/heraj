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
import hera.api.tupleorerror.ResultOrError;

@ApiAudience.Public
@ApiStability.Unstable
public interface AccountEitherOperation {

  /**
   * Get account state by account.
   *
   * @param account account
   * @return an account state or error
   */
  ResultOrError<AccountState> getState(Account account);

  /**
   * Get account state by address.
   *
   * @param address account address
   * @return an account state or error
   */
  ResultOrError<AccountState> getState(AccountAddress address);

  /**
   * Create name info of an account.
   *
   * @param account an account
   * @param name an new name
   * @param nonce an nonce which is used in a transaction
   * @return a create name transaction hash or error
   */
  ResultOrError<TxHash> createName(Account account, String name, long nonce);

  /**
   * Update name info of an account.
   *
   * @param owner an name owner
   * @param name an already binded name
   * @param newOwner an new owner of name
   * @param nonce an nonce which is used in a transaction
   * @return a update name transaction hash or error
   */
  ResultOrError<TxHash> updateName(Account owner, String name, AccountAddress newOwner,
      long nonce);

  /**
   * Get owner of an account name.
   *
   * @param name an name of account
   * @return an account address binded with name or error
   */
  ResultOrError<AccountAddress> getNameOwner(String name);

  /**
   * Sign for transaction.
   *
   * @param account account to verify
   * @param rawTransaction raw transaction to sign
   * @return signed transaction or error
   */
  ResultOrError<Transaction> sign(Account account, RawTransaction rawTransaction);

  /**
   * Verify transaction.
   *
   * @param account account to verify
   * @param transaction transaction to verify
   * @return verify result or error
   */
  ResultOrError<Boolean> verify(Account account, Transaction transaction);

}
