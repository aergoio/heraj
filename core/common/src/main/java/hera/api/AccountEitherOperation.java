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
import hera.api.tupleorerror.ResultOrError;

@ApiAudience.Public
@ApiStability.Unstable
public interface AccountEitherOperation {

  /**
   * Get account state by address.
   *
   * @param address account address
   * @return an account state or error
   */
  ResultOrError<AccountState> getState(AccountAddress address);

  /**
   * Get account state by account.
   *
   * @param account account
   * @return an account state or error
   */
  default ResultOrError<AccountState> getState(Account account) {
    return getState(account.getAddress());
  }

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
