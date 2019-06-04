/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import java.util.List;

/**
 * Provide server keystore related operations. It provides followings:
 *
 * <ul>
 * <li>lookup stored accounts</li>
 * <li>locking / unlocking account</li>
 * <li>importing / exporting account</li>
 * </ul>
 *
 * @author Taeik Lim
 *
 */
@ApiAudience.Public
@ApiStability.Unstable
public interface KeyStoreOperation {

  /**
   * Get account list on a key store.
   *
   * @return account list
   */
  List<AccountAddress> list();

  /**
   * Create an accountwith password. The private key is stored in a server key store.
   *
   * @param password account password
   * @return created account
   */
  AccountAddress create(String password);

  /**
   * Lock an account whose key is in a server key store.
   *
   * @param authentication account authentication
   * @return lock result
   */
  boolean lock(Authentication authentication);

  /**
   * Unlock an account whose key is in a server key store.
   *
   * @param authentication account authentication
   * @return unlock result
   */
  boolean unlock(Authentication authentication);

  /**
   * Sign for transaction. A transaction sender should be unlocked.
   *
   * @param rawTransaction raw transaction to sign
   * @return signed transaction
   */
  Transaction sign(RawTransaction rawTransaction);

  /**
   * Import an encrypted private key to a server key store. An {@code oldPassword} is used to
   * decrypt private key passed by and an {@code newPassword} is used to store private key encrypted
   * in a server.
   *
   * @param encryptedKey an encrypted private key
   * @param oldPassword old password to decrypt encrypted private key
   * @param newPassword new password to store in a remote storage
   * @return an imported accoungt
   */
  AccountAddress importKey(EncryptedPrivateKey encryptedKey, String oldPassword,
      String newPassword);

  /**
   * Export an encrypted private key of account whose key is stored in a server key store.
   *
   * @param authentication account authentication
   * @return an encrypted private key
   */
  EncryptedPrivateKey exportKey(Authentication authentication);

}
