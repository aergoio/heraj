/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.exception.InvalidAuthentiationException;
import hera.key.AergoKey;
import java.util.List;

@ApiAudience.Private
@ApiStability.Unstable
public interface KeyStore {

  /**
   * Store an {@code AergoKey} to the storage.
   *
   * @param key an aergo key to store
   * @param password a password to encrypt the aergo key
   */
  void saveKey(AergoKey key, String password);

  /**
   * Export an private key encrypted.
   *
   * @param authentication an authentication to used in exporting key
   * @return an encrypted private key.
   * @throws InvalidAuthentiationException on failure
   */
  EncryptedPrivateKey export(Authentication authentication);

  /**
   * Get all the stored addresses.
   *
   * @return stored addresses.
   */
  List<AccountAddress> listStoredAddresses();

  /**
   * Unlock and return unlocked account.
   *
   * @param authentication an authentication which is used in unlocking account
   * @return an unlocked account.
   * @throws InvalidAuthentiationException on failure
   */
  Account unlock(Authentication authentication);

  /**
   * Lock an account corresponding to {@code authentication}.
   *
   * @param authentication an authentication which is used in locking account
   * @throws InvalidAuthentiationException on failure
   */
  void lock(Authentication authentication);

  /**
   * Store the keystore to the path.
   *
   * @param path a path
   * @param password a password used in storing key store
   */
  void store(String path, String password);

}
