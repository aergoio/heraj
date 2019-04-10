/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore;

import static hera.util.AddressUtils.deriveAddress;
import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountFactory;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.Identity;
import hera.client.AergoClient;
import hera.exception.KeyStoreException;
import hera.key.AergoKey;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

@ApiAudience.Private
@ApiStability.Unstable
@RequiredArgsConstructor
public class ServerKeyStore implements KeyStore {

  protected final Logger logger = getLogger(getClass());

  protected final AergoClient aergoClient;

  @Override
  public void saveKey(final AergoKey key, final Authentication authentication) {
    try {
      logger.debug("Save key: {}, authentication: {}", key, authentication);
      if (!(authentication.getIdentity() instanceof AccountAddress)) {
        throw new UnsupportedOperationException(
            "Server keystore only support account address identity");
      }
      final String password = authentication.getPassword();
      aergoClient.getKeyStoreOperation().importKey(key.export(password), password, password);
    } catch (final Exception e) {
      throw new KeyStoreException(e);
    }
  }

  @Override
  public EncryptedPrivateKey export(final Authentication authentication) {
    try {
      logger.debug("Export key with authentication: {}", authentication);
      return aergoClient.getKeyStoreOperation().exportKey(authentication);
    } catch (final Exception e) {
      throw new KeyStoreException(e);
    }
  }

  @Override
  public List<Identity> listIdentities() {
    try {
      final List<Identity> identifications = new ArrayList<Identity>();
      for (final AccountAddress accountAddress : aergoClient.getKeyStoreOperation().list()) {
        identifications.add(accountAddress);
      }
      return identifications;
    } catch (final Exception e) {
      throw new KeyStoreException(e);
    }
  }

  @Override
  public synchronized Account unlock(final Authentication authentication) {
    try {
      logger.debug("Unlock key with authentication: {}", authentication);
      final boolean unlocked = aergoClient.getKeyStoreOperation().unlock(authentication);
      if (!unlocked) {
        return null;
      }
      final AccountAddress derivedAddress = deriveAddress(authentication.getIdentity());
      return new AccountFactory().create(derivedAddress);
    } catch (final Exception e) {
      logger.debug("Unlock failed by {}", e.toString());
      return null;
    }
  }

  @Override
  public synchronized boolean lock(final Authentication authentication) {
    try {
      logger.debug("Lock key with authentication: {}", authentication);
      return aergoClient.getKeyStoreOperation().lock(authentication);
    } catch (final Exception e) {
      throw new KeyStoreException(e);
    }
  }

  @Override
  public void store(final String path, final String password) {
    // do nothing
  }

}
