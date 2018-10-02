/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.exception.AdaptException;
import java.util.List;

@ApiAudience.Public
@ApiStability.Unstable
public interface AccountOperation {

  /**
   * Get account list.
   *
   * @return account list
   */
  List<Account> list();

  /**
   * Create an account with password.
   *
   * @param password account password
   * @return created account
   */
  Account create(String password);

  /**
   * Get account by address.
   *
   * @param address account address
   * @return an account
   */
  Account get(AccountAddress address);

  /**
   * Get account by account.
   *
   * @param account account
   * @return an account
   */
  default Account get(Account account) {
    return account.adapt(AccountAddress.class).map(a -> get(a))
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
   * Import an encrypted private key. An {@code password} is used to decrypt private key passed by
   * and store private key encrypted in a server.
   *
   * @param encryptedKey an encrypted private key
   * @param password password to decrypt encrypted private key and store encrypted in a remote
   *        storage
   * @return account result
   */
  default Account importKey(EncryptedPrivateKey encryptedKey, String password) {
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
  Account importKey(EncryptedPrivateKey encryptedKey, String oldPassword, String newPassword);

  /**
   * Export an encrypted private key of account.
   *
   * @param authentication account authentication
   * @return an encrypted private key
   */
  EncryptedPrivateKey exportKey(Authentication authentication);
}
