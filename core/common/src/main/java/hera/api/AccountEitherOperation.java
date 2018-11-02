/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import static hera.api.tupleorerror.FunctionChain.fail;

import hera.ContextAware;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.tupleorerror.ResultOrError;
import hera.exception.AdaptException;
import java.util.List;

@ApiAudience.Public
@ApiStability.Unstable
public interface AccountEitherOperation extends ContextAware {

  /**
   * Get account list.
   *
   * @return account list or error
   */
  ResultOrError<List<AccountAddress>> list();

  /**
   * Create an account with password. The private key is stored in a server key store.
   *
   * @param password account password
   * @return created account or error
   */
  ResultOrError<Account> create(String password);

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
   * Lock an account whose key is in a server key store.
   *
   * @param authentication account authentication
   * @return lock result or error
   */
  ResultOrError<Boolean> lock(Authentication authentication);

  /**
   * Unlock an account whose key is in a server key store.
   *
   * @param authentication account authentication
   * @return unlock result or error
   */
  ResultOrError<Boolean> unlock(Authentication authentication);

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

  /**
   * Import an encrypted private key to a server key store. An {@code password} is used to decrypt
   * private key passed by and store private key encrypted in a server.
   *
   * @param encryptedKey an encrypted private key
   * @param password password to decrypt encrypted private key and store encrypted in a remote
   *        storage
   * @return account result or error
   */
  default ResultOrError<Account> importKey(EncryptedPrivateKey encryptedKey, String password) {
    return importKey(encryptedKey, password, password);
  }

  /**
   * Import an encrypted private key to a server key store. An {@code oldPassword} is used to
   * decrypt private key passed by and an {@code newPassword} is used to store private key encrypted
   * in a server.
   *
   * @param encryptedKey an encrypted private key
   * @param oldPassword old password to decrypt encrypted private key
   * @param newPassword new password to store in a remote storage
   * @return account result or error
   */
  ResultOrError<Account> importKey(EncryptedPrivateKey encryptedKey, String oldPassword,
      String newPassword);

  /**
   * Export an encrypted private key of account whose key is stored in a server key store.
   *
   * @param authentication account authentication
   * @return an encrypted private key
   */
  ResultOrError<EncryptedPrivateKey> exportKey(Authentication authentication);
}
