/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore;

import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.Identity;
import hera.exception.KeyStoreException;
import hera.key.AergoKey;
import hera.key.Signer;
import java.util.List;

public interface KeyStore extends Signer {

  /**
   * Unlock and return unlocked account.
   *
   * @param authentication an authentication which is used in unlocking account
   * @return an unlocked account
   *
   * @throws KeyStoreException on keystore error
   */
  boolean unlock(Authentication authentication);

  /**
   * Lock an account corresponding to {@code authentication}.
   *
   * @param authentication an authentication which is used in locking account
   * @return a lock result
   *
   * @throws KeyStoreException on keystore error
   */
  boolean lock(Authentication authentication);

  /**
   * Store an {@code AergoKey} to the keystore.
   *
   * @param authentication an authentication to save key
   * @param key an aergo key to store
   *
   * @throws KeyStoreException on keystore error
   */
  void save(Authentication authentication, AergoKey key);

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

}
