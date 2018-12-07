/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.Transaction;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.exception.AdaptException;

@ApiAudience.Public
@ApiStability.Unstable
public interface AccountAsyncOperation {

  /**
   * Get account state by address asynchronously.
   *
   * @param address account address
   * @return future of an account state or error
   */
  ResultOrErrorFuture<AccountState> getState(AccountAddress address);

  /**
   * Get account state by account.
   *
   * @param account account
   * @return future of an account state or error
   */
  default ResultOrErrorFuture<AccountState> getState(Account account) {
    return account.adapt(AccountAddress.class).map(this::getState)
        .orElse(ResultOrErrorFutureFactory.supply(() -> {
          throw new AdaptException(account.getClass(), AccountAddress.class);
        }));
  }

  /**
   * Sign for transaction asynchronously.
   *
   * @param account account to sign
   * @param transaction transaction to sign
   * @return future of signed transaction or error
   */
  ResultOrErrorFuture<Transaction> sign(Account account, Transaction transaction);

  /**
   * Verify transaction asynchronously.
   *
   * @param account account to verify
   * @param transaction transaction to verify
   * @return future of verify result or error
   */
  ResultOrErrorFuture<Boolean> verify(Account account, Transaction transaction);

}
