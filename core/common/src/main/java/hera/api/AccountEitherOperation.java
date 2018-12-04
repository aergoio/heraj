/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import static hera.api.tupleorerror.FunctionChain.fail;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.tupleorerror.ResultOrError;
import hera.exception.AdaptException;

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
  @SuppressWarnings("unchecked")
  default ResultOrError<AccountState> getState(Account account) {
    return account.adapt(AccountAddress.class).map(this::getState)
        .orElse(fail(new AdaptException(account.getClass(), AccountAddress.class)));
  }

  /**
   * Sign for transaction.
   *
   * @param account account to verify
   * @param transaction transaction to sign
   * @return signing result or error
   */
  ResultOrError<Signature> sign(Account account, Transaction transaction);

  /**
   * Verify transaction.
   *
   * @param account account to verify
   * @param transaction transaction to verify
   * @return verify result or error
   */
  ResultOrError<Boolean> verify(Account account, Transaction transaction);

}
