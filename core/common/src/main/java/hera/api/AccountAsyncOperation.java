/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.exception.AdaptException;
import java.util.List;

@ApiAudience.Public
@ApiStability.Unstable
public interface AccountAsyncOperation {

  /**
   * Get account list asynchronously.
   *
   * @return future of account list or error
   */
  ResultOrErrorFuture<List<AccountAddress>> list();

  /**
   * Create an account with password asynchronously. The private key is stored in a server key
   * store.
   *
   * @param password account password
   * @return future of created account or error
   */
  ResultOrErrorFuture<Account> create(String password);

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
   * Lock an account asynchronously whose key is in a server key store.
   *
   * @param authentication account authentication
   * @return future of lock result or error
   */
  ResultOrErrorFuture<Boolean> lock(Authentication authentication);

  /**
   * Unlock an account asynchronously whose key is in a server key store.
   *
   * @param authentication account authentication
   * @return future of unlock result or error
   */
  ResultOrErrorFuture<Boolean> unlock(Authentication authentication);

  /**
   * Sign for transaction asynchronously.
   *
   * @param account account to sign
   * @param transaction transaction to sign
   * @return future of signing result or error
   */
  ResultOrErrorFuture<Signature> sign(Account account, Transaction transaction);

  /**
   * Verify transaction asynchronously.
   *
   * @param account account to verify
   * @param transaction transaction to verify
   * @return future of verify result or error
   */
  ResultOrErrorFuture<Boolean> verify(Account account, Transaction transaction);

  /**
   * Import an encrypted private key asynchronously. An {@code password} is used to decrypt private
   * key passed by and store private key encrypted in a server.
   *
   * @param encryptedKey an encrypted private key
   * @param password password to decrypt encrypted private key and store encrypted in a remote
   *        storage
   * @return future of account result or error
   */
  default ResultOrErrorFuture<Account> importKey(EncryptedPrivateKey encryptedKey,
      String password) {
    return importKey(encryptedKey, password, password);
  }

  /**
   * Import an encrypted private key asynchronously. An {@code oldPassword} is used to decrypt
   * private key passed by and an {@code newPassword} is used to store private key encrypted in a
   * server.
   *
   * @param encryptedKey an encrypted private key
   * @param oldPassword old password to decrypt encrypted private key
   * @param newPassword new password to store in a remote storage
   * @return future of account result or error
   */
  ResultOrErrorFuture<Account> importKey(EncryptedPrivateKey encryptedKey, String oldPassword,
      String newPassword);

  /**
   * Export an encrypted private key of account asynchronously whose key is stored in a server key
   * store.
   *
   * @param authentication account authentication
   * @return future of an encrypted private key
   */
  ResultOrErrorFuture<EncryptedPrivateKey> exportKey(Authentication authentication);
}
