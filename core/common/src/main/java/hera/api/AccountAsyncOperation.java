/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import static hera.api.tupleorerror.FunctionChain.fail;

import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.exception.AdaptException;
import java.util.List;

public interface AccountAsyncOperation {

  /**
   * Get account list asynchronously.
   *
   * @return future of account list or error
   */
  ResultOrErrorFuture<List<Account>> list();

  /**
   * Create an account with password asynchronously.
   *
   * @param password account password
   * @return future of created account or error
   */
  ResultOrErrorFuture<Account> create(String password);

  /**
   * Get account by address asynchronously.
   *
   * @param address account address
   * @return future of an account or error
   */
  ResultOrErrorFuture<Account> get(AccountAddress address);

  /**
   * Get account by account.
   *
   * @param account account
   * @return future of an account or error
   */
  @SuppressWarnings("unchecked")
  default ResultOrErrorFuture<Account> get(Account account) {
    return account.adapt(AccountAddress.class).map(a -> get(a)).orElse(ResultOrErrorFutureFactory
        .supply(() -> fail(new AdaptException(account.getClass(), AccountAddress.class))));
  }

  /**
   * Lock an account asynchronously.
   *
   * @param authentication account authentication
   * @return future of lock result or error
   */
  ResultOrErrorFuture<Boolean> lock(Authentication authentication);

  /**
   * Unlock an account asynchronously.
   *
   * @param authentication account authentication
   * @return future of unlock result or error
   */
  ResultOrErrorFuture<Boolean> unlock(Authentication authentication);
}
