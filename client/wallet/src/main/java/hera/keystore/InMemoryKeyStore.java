/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore;

import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountFactory;
import hera.api.model.Authentication;
import hera.api.model.BytesValue;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.Identity;
import hera.api.model.RawTransaction;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.exception.KeyStoreException;
import hera.key.AergoKey;
import hera.key.Signer;
import hera.util.Sha256Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;

@ApiAudience.Private
@ApiStability.Unstable
public class InMemoryKeyStore implements KeyStore, Signer {

  protected final Logger logger = getLogger(getClass());

  protected Map<Authentication, EncryptedPrivateKey> auth2EncryptedPrivateKey =
      new ConcurrentHashMap<Authentication, EncryptedPrivateKey>();

  protected Authentication currentUnlockedAuth;
  protected AergoKey currentUnlockedKey;

  @Override
  public void saveKey(final AergoKey key, final Authentication authentication) {
    try {
      logger.debug("Save key: {}, authentication: {}", authentication);
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
  public Account unlock(final Authentication authentication) {
    try {
      logger.debug("Unlock key with authentication: {}", authentication);
      final Authentication digestedAuth = digest(authentication);
      if (!auth2EncryptedPrivateKey.containsKey(digestedAuth)) {
        return null;
      }
      final EncryptedPrivateKey encrypted = auth2EncryptedPrivateKey.get(digestedAuth);
      final AergoKey key = AergoKey.of(encrypted, authentication.getPassword());
      currentUnlockedAuth = digestedAuth;
      currentUnlockedKey = key;
      return new AccountFactory().create(key.getAddress(), this);
    } catch (Exception e) {
      logger.debug("Unlock failed by {}", e.toString());
      return null;
    }
  }

  @Override
  public boolean lock(final Authentication authentication) {
    try {
      logger.debug("Lock key with authentication: {}", authentication);
      final Authentication digested = digest(authentication);
      if (!currentUnlockedAuth.equals(digested)) {
        return false;
      }
      currentUnlockedAuth = null;
      currentUnlockedKey = null;
      return true;
    } catch (Exception e) {
      throw new KeyStoreException(e);
    }
  }

  protected Authentication digest(final Authentication rawAuthentication) {
    final byte[] digestedPassword = Sha256Utils.digest(rawAuthentication.getPassword().getBytes());
    return Authentication.of(rawAuthentication.getIdentity(), new String(digestedPassword));
  }

  @Override
  public Transaction sign(final RawTransaction rawTransaction) {
    try {
      logger.debug("Sign raw transaction: {}", rawTransaction);
      final AccountAddress sender = rawTransaction.getSender();
      if (null == sender) {
        throw new IllegalArgumentException("Sender is null");
      }
      final AergoKey key = getUnlockedKey();
      return key.sign(rawTransaction);
    } catch (Exception e) {
      throw new KeyStoreException(e);
    }
  }

  @Override
  public Signature sign(final BytesValue plainText) {
    try {
      logger.debug("Sign plain text: {}", plainText);
      final AergoKey key = getUnlockedKey();
      return key.sign(plainText);
    } catch (Exception e) {
      throw new KeyStoreException(e);
    }
  }

  @Override
  public String signMessage(final String message) {
    try {
      logger.debug("Sign message: {}", message);
      final AergoKey key = getUnlockedKey();
      return key.signMessage(message);
    } catch (Exception e) {
      throw new KeyStoreException(e);
    }
  }

  protected AergoKey getUnlockedKey() {
    if (null == currentUnlockedKey) {
      throw new IllegalStateException("Unlock account first");
    }
    return currentUnlockedKey;
  }

  @Override
  public void store(final String path, final String password) {
    // do nothing
  }

}

