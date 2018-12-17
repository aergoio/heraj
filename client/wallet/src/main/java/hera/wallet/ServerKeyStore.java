/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.Account;
import hera.api.model.AccountFactory;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.client.AergoClient;
import hera.exception.WalletException;
import hera.key.AergoKey;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

@RequiredArgsConstructor
public class ServerKeyStore implements KeyStore {

  protected final Logger logger = getLogger(getClass());

  protected final AergoClient aergoClient;

  @Override
  public void save(final AergoKey key, final String password) {
    try {
      aergoClient.getKeyStoreOperation().importKey(key.export(password), password);
    } catch (final Exception e) {
      throw new WalletException(e);
    }
  }

  @Override
  public EncryptedPrivateKey export(final Authentication authentication) {
    try {
      return aergoClient.getKeyStoreOperation().exportKey(authentication);
    } catch (final Exception e) {
      throw new WalletException(e);
    }
  }

  @Override
  public Account unlock(final Authentication authentication) {
    try {
      final boolean unlocked = aergoClient.getKeyStoreOperation().unlock(authentication);
      return unlocked ? new AccountFactory().create(authentication.getAddress()) : null;
    } catch (Exception e) {
      logger.info("Key unlocking failure with error : {}", e.getMessage());
      return null;
    }
  }

  @Override
  public boolean lock(final Authentication authentication) {
    try {
      return aergoClient.getKeyStoreOperation().lock(authentication);
    } catch (Exception e) {
      logger.info("Key locking failure with error : {}", e.getMessage());
      return false;
    }
  }

}
