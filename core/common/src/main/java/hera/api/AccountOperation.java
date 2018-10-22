/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.ContextAware;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.ServerManagedAccount;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.exception.AdaptException;
import java.util.List;

@ApiAudience.Public
@ApiStability.Unstable
public interface AccountOperation extends ContextAware {

  /**
   * Get account list.
   *
   * @return account list
   */
  List<AccountAddress> list();

  /**
   * Create an account with password.
   *
   * @param password account password
   * @return created account
   */
  ServerManagedAccount create(String password);

  /**
   * Get account state by address.
   *
   * @param address account address
   * @return an account state
   */
  AccountState getState(AccountAddress address);

  /**
   * Get account state by account.
   *
   * @param account account
   * @return an account state
   */
  default AccountState getState(Account account) {
    return account.adapt(AccountAddress.class).map(this::getState)
        .orElseThrow(() -> new AdaptException(account.getClass(), AccountAddress.class));
  }

  /**
   * Lock an account.
   *
   * @param authentication account authentication
   * @return lock result
   */
  boolean lock(Authentication authentication);

  /**
   * Unlock an account.
   *
   * @param authentication account authentication
   * @return unlock result
   */
  boolean unlock(Authentication authentication);

  /**
   * Sign for transaction.
   *
   * @param account account to sign
   * @param transaction transaction to sign
   * @return signing result
   */
  Signature sign(Account account, Transaction transaction);

  /**
   * Verify transaction.
   *
   * @param account account to verify
   * @param transaction transaction to verify
   * @return verify result
   */
  boolean verify(Account account, Transaction transaction);

  /**
   * Import an encrypted private key. An {@code password} is used to decrypt private key passed by
   * and store private key encrypted in a server.
   *
   * @param encryptedKey an encrypted private key
   * @param password password to decrypt encrypted private key and store encrypted in a remote
   *        storage
   * @return account result
   */
  default ServerManagedAccount importKey(EncryptedPrivateKey encryptedKey, String password) {
    return importKey(encryptedKey, password, password);
  }

  /**
   * Import an encrypted private key. An {@code oldPassword} is used to decrypt private key passed
   * by and an {@code newPassword} is used to store private key encrypted in a server.
   *
   * @param encryptedKey an encrypted private key
   * @param oldPassword old password to decrypt encrypted private key
   * @param newPassword new password to store in a remote storage
   * @return account result
   */
  ServerManagedAccount importKey(EncryptedPrivateKey encryptedKey, String oldPassword,
      String newPassword);

  /**
   * Export an encrypted private key of account.
   *
   * @param authentication account authentication
   * @return an encrypted private key
   */
  EncryptedPrivateKey exportKey(Authentication authentication);

}
