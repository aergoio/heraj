/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore;

import hera.api.model.Account;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.Identity;
import hera.exception.KeyStoreException;
import hera.key.AergoKey;
import java.util.List;

public interface KeyStore {

  /**
   * Store an {@code AergoKey} to the storage.
   *
   * @param key an aergo key to store
   * @param authentication an authentication to save key
   *
   * @throws KeyStoreException on keystore error
   */
  void saveKey(AergoKey key, Authentication authentication);

  /**
   * Export an private key encrypted.
   *
   * @param authentication an authentication to used in exporting key
   * @return an encrypted private key.
   *
   * @throws KeyStoreException on keystore error
   */
  EncryptedPrivateKey export(Authentication authentication);

  /**
   * Get all the stored identities.
   *
   * @return stored identities.
   *
   * @throws KeyStoreException on keystore error
   */
  List<Identity> listIdentities();

  /**
   * Unlock and return unlocked account.
   *
   * @param authentication an authentication which is used in unlocking account
   * @return an unlocked account. null if failure
   *
   * @throws KeyStoreException on keystore error
   */
  Account unlock(Authentication authentication);

  /**
   * Lock an account corresponding to {@code authentication}.
   *
   * @param authentication an authentication which is used in locking account
   * @return a locked account.
   *
   * @throws KeyStoreException on keystore error
   */
  boolean lock(Authentication authentication);

  /**
   * Store the keystore to the path.
   *
   * @param path a path
   * @param password a password used in storing key store
   *
   * @throws KeyStoreException on keystore error
   */
  void store(String path, String password);

}
