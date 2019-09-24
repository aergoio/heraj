/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.Identity;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.exception.KeyStoreException;
import hera.key.AergoKey;
import java.util.List;

@ApiAudience.Public
@ApiStability.Unstable
public interface KeyStore {

  /**
   * Unlock an account corresponding to {@code authentication}.
   *
   * @param authentication an authentication to unlock account
   * @return an unlocked account
   *
   * @throws KeyStoreException on keystore error
   */
  AccountAddress unlock(Authentication authentication);

  /**
   * Lock an account corresponding to {@code authentication}.
   *
   * @param authentication an authentication which is used in locking account
   * @return a lock result
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

  /**
   * Store keystore to the {@code path} with {@code password}.
   *
   * @param path a path to store keystore
   * @param password a password to encrypt keystore
   */
  void store(String path, char[] password);

  /**
   * Sign to raw transaction.
   *
   * @param unlocked an account to sign
   * @param rawTransaction raw transaction to sign
   * @return a signed transaction
   *
   * @throws KeyStoreException on signing error
   */
  Transaction sign(AccountAddress unlocked, RawTransaction rawTransaction);

}
