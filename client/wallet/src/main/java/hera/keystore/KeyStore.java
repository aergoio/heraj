/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.Identity;
import hera.exception.InvalidAuthenticationException;
import hera.key.AergoKey;
import hera.key.Signer;
import java.util.List;

@ApiAudience.Public
@ApiStability.Unstable
public interface KeyStore {

  /**
   * Store an {@code AergoKey} to the keystore.
   *
   * @param authentication an authentication to save key
   * @param key            an aergo key to store
   * @throws InvalidAuthenticationException on invalid authentication
   */
  void save(Authentication authentication, AergoKey key);

  /**
   * Load signer with {@code authentication}.
   *
   * @param authentication an authentication
   * @return a signer corresponding to authentication
   * @throws InvalidAuthenticationException on invalid authentication
   */
  Signer load(Authentication authentication);

  /**
   * Remove an {@code AergoKey} corresponding to {@code authentication}.
   *
   * @param authentication an authentication to remove key
   * @throws InvalidAuthenticationException on invalid authentication
   */
  void remove(Authentication authentication);

  /**
   * Export an private key encrypted.
   *
   * @param authentication an authentication to used in exporting key
   * @param password       a password to encrypt
   * @return an encrypted private key
   * @throws InvalidAuthenticationException on invalid authentication
   */
  EncryptedPrivateKey export(Authentication authentication, String password);

  /**
   * Get all the stored identities.
   *
   * @return stored identities
   */
  List<Identity> listIdentities();

  /**
   * Store keystore to the {@code path} with {@code password}.
   *
   * @param path     a path to store keystore
   * @param password a password to encrypt keystore
   */
  void store(String path, char[] password);

}
