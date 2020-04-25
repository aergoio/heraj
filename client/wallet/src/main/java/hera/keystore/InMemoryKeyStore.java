/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore;

import static hera.util.ValidationUtils.assertNotNull;
import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.Identity;
import hera.exception.HerajException;
import hera.exception.InvalidAuthenticationException;
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
public class InMemoryKeyStore extends AbstractKeyStore implements KeyStore {

  protected final Object lock = new Object();
  protected final Set<Identity> storedIdentities = new HashSet<>();
  protected final Map<String, EncryptedPrivateKey> hashedAuth2Encrypted = new HashMap<>();

  @Override
  public void save(final Authentication authentication, final AergoKey key) {
    try {
      assertNotNull(authentication, "Authentication must not null");
      assertNotNull(key, "Save key must not null");
      logger.debug("Save with authentication: {}, key: {}", authentication, key.getAddress());

      final String digested = digest(authentication);
      synchronized (lock) {
        if (this.storedIdentities.contains(authentication.getIdentity())) {
          throw new InvalidAuthenticationException("Identity already exists");
        }
        if (this.hashedAuth2Encrypted.containsKey(digested)) {
          throw new InvalidAuthenticationException("Invalid authentication");
        }

        final EncryptedPrivateKey encrypted = key.export(authentication.getPassword());
        logger.trace("Encrypted key: {}", encrypted);
        this.storedIdentities.add(authentication.getIdentity());
        this.hashedAuth2Encrypted.put(digested, encrypted);
      }
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public Signer load(final Authentication authentication) {
    try {
      assertNotNull(authentication, "Authentication must not null");
      logger.debug("Load with authentication: {}", authentication);

      final String digested = digest(authentication);
      EncryptedPrivateKey encrypted;
      synchronized (lock) {
        if (!this.hashedAuth2Encrypted.containsKey(digested)) {
          throw new InvalidAuthenticationException("Invalid authentication");
        }

        encrypted = this.hashedAuth2Encrypted.get(digested);
        logger.trace("Encrypted: {}", encrypted);
      }

      final AergoKey decrypted = AergoKey.of(encrypted, authentication.getPassword());
      logger.trace("Decrypted address: {}", decrypted.getAddress());
      return decrypted;
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public void remove(final Authentication authentication) {
    try {
      assertNotNull(authentication, "Authentication must not null");
      logger.debug("Remove with authentication: {}", authentication);

      final String digested = digest(authentication);
      synchronized (lock) {
        if (!this.hashedAuth2Encrypted.containsKey(digested)) {
          throw new InvalidAuthenticationException("Invalid authentication");
        }

        final Identity identity = authentication.getIdentity();
        this.storedIdentities.remove(identity);
        this.hashedAuth2Encrypted.remove(digested);
      }
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public EncryptedPrivateKey export(final Authentication authentication, final String password) {
    try {
      assertNotNull(authentication, "Authentication must not null");
      assertNotNull(password, "Password must not null");
      logger.debug("Export with authentication: {}, password: ***", authentication);

      final String digested = digest(authentication);
      EncryptedPrivateKey encrypted;
      synchronized (lock) {
        if (!this.hashedAuth2Encrypted.containsKey(digested)) {
          throw new InvalidAuthenticationException("Invalid authentication");
        }

        encrypted = this.hashedAuth2Encrypted.get(digested);
      }

      final AergoKey decrypted = AergoKey.of(encrypted, authentication.getPassword());
      return decrypted.export(password);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public List<Identity> listIdentities() {
    try {
      List<Identity> identities;
      synchronized (lock) {
        identities = new ArrayList<>(this.storedIdentities);
      }
      return identities;
    } catch (Exception e) {
      throw converter.convert(e);
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
