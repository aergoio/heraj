/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Account;
import hera.api.model.AccountFactory;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.Identity;
import hera.exception.KeyStoreException;
import hera.key.AergoKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApiAudience.Private
@ApiStability.Unstable
public class InMemoryKeyStore extends AbstractKeyStore {

  protected Map<Authentication, EncryptedPrivateKey> auth2EncryptedPrivateKey =
      new ConcurrentHashMap<Authentication, EncryptedPrivateKey>();

  @Override
  public void saveKey(final AergoKey key, final Authentication authentication) {
    try {
      logger.debug("Save key: {}, authentication: {}", key, authentication);
      final EncryptedPrivateKey encryptedKey = key.export(authentication.getPassword());
      final Authentication digestedAuth = digest(authentication);
      auth2EncryptedPrivateKey.put(digestedAuth, encryptedKey);
    } catch (final Exception e) {
      throw new KeyStoreException(e);
    }
  }

  @Override
  public EncryptedPrivateKey export(final Authentication authentication) {
    try {
      logger.debug("Export key with authentication: {}", authentication);
      final Authentication digestedAuth = digest(authentication);
      if (!auth2EncryptedPrivateKey.containsKey(digestedAuth)) {
        throw new IllegalArgumentException("A key mappged with Authentication isn't exist");
      }
      return auth2EncryptedPrivateKey.get(digestedAuth);
    } catch (final Exception e) {
      throw new KeyStoreException(e);
    }
  }

  @Override
  public List<Identity> listIdentities() {
    try {
      final List<Identity> identities = new ArrayList<Identity>();
      for (final Authentication authentication : auth2EncryptedPrivateKey.keySet()) {
        identities.add(authentication.getIdentity());
      }
      return identities;
    } catch (final Exception e) {
      throw new KeyStoreException(e);
    }
  }

  @Override
  public synchronized Account unlock(final Authentication authentication) {
    try {
      logger.debug("Unlock key with authentication: {}", authentication);
      final Authentication digestedAuth = digest(authentication);
      if (!auth2EncryptedPrivateKey.containsKey(digestedAuth)) {
        return null;
      }
      final EncryptedPrivateKey encrypted = auth2EncryptedPrivateKey.get(digestedAuth);
      final AergoKey key = AergoKey.of(encrypted, authentication.getPassword());
      currentlyUnlockedAuth = digestedAuth;
      currentlyUnlockedKey = key;
      return new AccountFactory().create(key.getAddress(), this);
    } catch (Exception e) {
      logger.debug("Unlock failed by {}", e.toString());
      return null;
    }
  }

  @Override
  public void store(final String path, final String password) {
    // do nothing
  }

}

