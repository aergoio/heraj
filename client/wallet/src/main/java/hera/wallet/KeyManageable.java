/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import hera.api.model.Authentication;
import hera.api.model.Identity;
import hera.key.AergoKey;
import java.io.Closeable;
import java.util.List;

@Deprecated
public interface KeyManageable extends Closeable {

  /**
   * Bind a keystore with wallet. This operation has a meaning only for {@link WalletType#Secure}.
   * For other wallet type, do nothing.
   *
   * @param keyStore a java keystore
   */
  void bindKeyStore(java.security.KeyStore keyStore);

  /**
   * Save an aergo key to the key store. This operation has no meaning to {@link WalletType#Naive}.
   *
   * @param aergoKey an aergo key
   * @param password an encrypt key
   */
  void saveKey(AergoKey aergoKey, String password);

  /**
   * Save an aergo key to the key store. This operation has no meaning to {@link WalletType#Naive}.
   *
   * @param aergoKey an aergo key
   * @param identity an identity to save key
   * @param password an encrypt key
   */
  void saveKey(AergoKey aergoKey, Identity identity, String password);

  /**
   * Export an aergo key of a current account with encrypted.
   *
   * @param authentication an authentication
   * @return encoded encrypted private key
   */
  String exportKey(Authentication authentication);

  /**
   * Get all the stored identities in a binded key store.
   *
   * @return stored identities
   */
  List<Identity> listKeyStoreIdentities();

  /**
   * Unlock account with {@code Authentication}.
   *
   * @param authentication an authentication
   * @return unlock result
   */
  boolean unlock(Authentication authentication);

  /**
   * Lock current account with {@code Authentication}.
   *
   * @param authentication an authentication
   * @return unlock result
   */
  boolean lock(Authentication authentication);

  /**
   * Store the keystore to the path. This operation has a meaning only for {@link
   * WalletType#Secure}. For other wallet type, do nothing.
   *
   * @param path     a path
   * @param password a password used in storing key store
   */
  void storeKeyStore(String path, String password);

  /**
   * {@inheritDoc}
   */
  void close();

}
