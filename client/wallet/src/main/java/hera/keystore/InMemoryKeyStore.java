/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.Identity;
import hera.exception.KeyStoreException;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApiAudience.Public
@ApiStability.Unstable
public class InMemoryKeyStore extends AbstractKeyStore {

  protected final Map<Authentication, EncryptedPrivateKey> auth2Encrypted =
      new HashMap<>();

  protected AergoKey loadAergoKey(final Authentication authentication) {
    final Authentication digested = digest(authentication);
    if (!auth2Encrypted.containsKey(digested)) {
      throw new IllegalArgumentException("No such authentication");
    }

    final EncryptedPrivateKey encrypted = auth2Encrypted.get(digested);
    return new AergoKeyGenerator().create(encrypted, authentication.getPassword());
  }

  @Override
  public void save(final Authentication authentication, final AergoKey key) {
    try {
      logger.debug("Save key {} with authentication: {}", key, authentication);
      final Authentication digested = digest(authentication);

      synchronized (this) {
        if (auth2Encrypted.containsKey(digested)) {
          throw new IllegalArgumentException("Authentication already exists");
        }

        final EncryptedPrivateKey encryptedKey = key.export(authentication.getPassword());
        auth2Encrypted.put(digested, encryptedKey);
      }
    } catch (final Exception e) {
      throw new KeyStoreException(e);
    }
  }

  @Override
  public EncryptedPrivateKey export(final Authentication authentication) {
    try {
      logger.debug("Export key with authentication: {}", authentication);
      final Authentication digested = digest(authentication);
      if (!auth2Encrypted.containsKey(digested)) {
        throw new IllegalArgumentException("Invalid authentication");
      }

      return auth2Encrypted.get(digested);
    } catch (final Exception e) {
      throw new KeyStoreException(e);
    }
  }

  @Override
  public List<Identity> listIdentities() {
    try {
      final List<Identity> identities = new ArrayList<>();
      for (final Authentication authentication : auth2Encrypted.keySet()) {
        identities.add(authentication.getIdentity());
      }

      return identities;
    } catch (final Exception e) {
      throw new KeyStoreException(e);
    }
  }

  @Override
  public void store(final String path, final char[] password) {
    // do nothing
  }

}
