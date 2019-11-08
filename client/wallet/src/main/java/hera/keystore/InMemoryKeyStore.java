/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore;

import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.Identity;
import hera.exception.InvalidAuthenticationException;
import hera.exception.KeyStoreException;
import hera.key.AergoKey;
import hera.key.Signer;
import hera.util.Sha256Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;

@ApiAudience.Public
@ApiStability.Unstable
public class InMemoryKeyStore implements KeyStore {

  protected final transient Logger logger = getLogger(getClass());

  protected final Set<Identity> storedIdentities = new HashSet<>();
  protected final Map<String, EncryptedPrivateKey> hashedAuth2Encrypted = new HashMap<>();

  @Override
  public void save(final Authentication authentication, final AergoKey key) {
    try {
      logger.debug("Save with authentication: {}, key: {}", authentication, key);
      final String digested = digest(authentication);

      synchronized (this) {
        if (this.storedIdentities.contains(authentication.getIdentity())) {
          throw new InvalidAuthenticationException("Identity already exists");
        }
        if (this.hashedAuth2Encrypted.containsKey(digested)) {
          throw new InvalidAuthenticationException("Invalid authentication");
        }

        final EncryptedPrivateKey encryptedKey = key.export(authentication.getPassword());
        this.storedIdentities.add(authentication.getIdentity());
        this.hashedAuth2Encrypted.put(digested, encryptedKey);
      }
    } catch (InvalidAuthenticationException e) {
      throw e;
    } catch (final Exception e) {
      throw new KeyStoreException(e);
    }
  }

  @Override
  public Signer load(final Authentication authentication) {
    try {
      logger.debug("Unlock with authentication: {}", authentication);
      final String digested = digest(authentication);

      synchronized (this) {
        if (false == this.hashedAuth2Encrypted.containsKey(digested)) {
          throw new InvalidAuthenticationException("Invalid authentication");
        }

        final EncryptedPrivateKey encrypted = hashedAuth2Encrypted.get(digested);
        final AergoKey decrypted = AergoKey.of(encrypted, authentication.getPassword());
        return decrypted;
      }
    } catch (InvalidAuthenticationException e) {
      throw e;
    } catch (Exception e) {
      throw new KeyStoreException(e);
    }
  }

  @Override
  public void remove(final Authentication authentication) {
    try {
      logger.debug("Remove aergo key with authentication: {}", authentication);
      final String digested = digest(authentication);

      synchronized (this) {
        if (false == this.hashedAuth2Encrypted.containsKey(digested)) {
          throw new InvalidAuthenticationException("Invalid authentication");
        }

        this.storedIdentities.remove(authentication.getIdentity());
        this.hashedAuth2Encrypted.remove(digested);
      }
    } catch (InvalidAuthenticationException e) {
      throw e;
    } catch (final Exception e) {
      throw new KeyStoreException(e);
    }
  }

  @Override
  public EncryptedPrivateKey export(final Authentication authentication, final String password) {
    try {
      logger.debug("Export key with authentication: {}", authentication);
      final String digested = digest(authentication);

      synchronized (this) {
        if (false == hashedAuth2Encrypted.containsKey(digested)) {
          throw new InvalidAuthenticationException("Invalid authentication");
        }

        final EncryptedPrivateKey encrypted = hashedAuth2Encrypted.get(digested);
        final AergoKey decrypted = AergoKey.of(encrypted, authentication.getPassword());
        return decrypted.export(password);
      }
    } catch (InvalidAuthenticationException e) {
      throw e;
    } catch (final Exception e) {
      throw new KeyStoreException(e);
    }
  }

  @Override
  public List<Identity> listIdentities() {
    try {
      return new ArrayList<>(this.storedIdentities);
    } catch (final Exception e) {
      throw new KeyStoreException(e);
    }
  }

  @Override
  public void store(final String path, final char[] password) {
    // do nothing
  }

  protected String digest(final Authentication authentication) {
    final byte[] rawIdentity = authentication.getIdentity().getValue().getBytes();
    final byte[] rawPassword = authentication.getPassword().getBytes();
    final byte[] plaintext = new byte[rawIdentity.length + rawPassword.length];
    System.arraycopy(rawIdentity, 0, plaintext, 0, rawIdentity.length);
    System.arraycopy(rawPassword, 0, plaintext, rawIdentity.length, rawPassword.length);
    return new String(Sha256Utils.digest(plaintext));
  }

}
