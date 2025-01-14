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

/**
 * Keystore is an abstract interface of the repository that keeps {@link AergoKey}s.
 * A Keystore can contain multiple AergoKey, which is identified by {@link Authentication}.
 */
@ApiAudience.Public
@ApiStability.Unstable
public interface KeyStore {

  /**
   * Add an {@code AergoKey} to the keystore.
   * This method will throw {@link InvalidAuthenticationException} if the same key was already stored in the keystore.
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
   * Export a private key in an encrypted form
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
   * Return whether it contains the key.
   * @param identity identity of key
   * @return whether the key exists in this keystore
   */
  boolean contains(Identity identity);

  /**
   * Store keystore to the {@code path} with {@code password}.
   *
   * @param path     a path to store keystore
   * @param password a password to encrypt keystore
   */
  void store(String path, char[] password);

}
