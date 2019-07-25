/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore;

import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.Identity;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.client.AergoClient;
import hera.exception.KeyStoreException;
import hera.key.AergoKey;
import hera.wallet.internal.ClientInjectable;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.Setter;
import org.slf4j.Logger;

@ApiAudience.Private
@ApiStability.Unstable
public class ServerKeyStore implements KeyStore, ClientInjectable {

  protected final Logger logger = getLogger(getClass());

  @Setter
  @NonNull
  protected AergoClient client;

  protected AccountAddress currentlyUnlocked;

  @Override
  public synchronized boolean unlock(final Authentication authentication) {
    try {
      logger.debug("Unlock key with authentication: {}", authentication);
      if (!(authentication.getIdentity() instanceof AccountAddress)) {
        throw new IllegalArgumentException(
            "Server keystore only support account address identity");
      }

      final boolean result = client.getKeyStoreOperation().unlock(authentication);
      if (true == result) {
        currentlyUnlocked = (AccountAddress) authentication.getIdentity();
      }
      return result;
    } catch (final Exception e) {
      logger.info("Unlock failed by {}", e.toString());
      return false;
    }
  }

  @Override
  public synchronized boolean lock(final Authentication authentication) {
    try {
      logger.debug("Lock key with authentication: {}", authentication);
      if (!(authentication.getIdentity() instanceof AccountAddress)) {
        throw new IllegalArgumentException(
            "Server keystore only support account address identity");
      }

      final boolean result = client.getKeyStoreOperation().lock(authentication);
      if (true == result) {
        currentlyUnlocked = null;
      }
      return result;
    } catch (final Exception e) {
      logger.info("Lock failed by {}", e.toString());
      return false;
    }
  }

  @Override
  public void save(final Authentication authentication, final AergoKey key) {
    try {
      logger.debug("Save key: {}, authentication: {}", key, authentication);
      if (!(authentication.getIdentity() instanceof AccountAddress)) {
        throw new IllegalArgumentException(
            "Server keystore only support account address identity");
      }

      final AccountAddress authAddress = (AccountAddress) authentication.getIdentity();
      if (!authAddress.equals(key.getAddress())) {
        throw new IllegalArgumentException(
            "Account address ot authentication must equals with "
                + "address of key in a server keystore");
      }

      final String password = authentication.getPassword();
      client.getKeyStoreOperation().importKey(key.export(password), password, password);
    } catch (final Exception e) {
      throw new KeyStoreException(e);
    }
  }

  @Override
  public EncryptedPrivateKey export(final Authentication authentication) {
    try {
      logger.debug("Export key with authentication: {}", authentication);
      return client.getKeyStoreOperation().exportKey(authentication);
    } catch (final Exception e) {
      throw new KeyStoreException(e);
    }
  }

  @Override
  public List<Identity> listIdentities() {
    try {
      final List<Identity> identities = new ArrayList<Identity>();
      for (final AccountAddress accountAddress : client.getKeyStoreOperation().list()) {
        identities.add(accountAddress);
      }
      return identities;
    } catch (final Exception e) {
      throw new KeyStoreException(e);
    }
  }

  @Override
  public AccountAddress getPrincipal() {
    return currentlyUnlocked;
  }

  @Override
  public Transaction sign(final RawTransaction rawTransaction) {
    if (null == currentlyUnlocked) {
      throw new KeyStoreException("No unlocked address");
    }
    if (!currentlyUnlocked.equals(rawTransaction.getSender())) {
      throw new KeyStoreException(
          "Currently unlocked one is different with sender of raw transaction");
    }
    return client.getKeyStoreOperation().sign(rawTransaction);
  }

}
