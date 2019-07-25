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
import hera.exception.KeyStoreException;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.key.Signer;
import hera.util.Sha256Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;

@ApiAudience.Private
@ApiStability.Unstable
public class InMemoryKeyStore implements KeyStore {

  protected final transient Logger logger = getLogger(getClass());

  protected Signer current;

  protected Authentication currentAuth;

  protected Map<Authentication, EncryptedPrivateKey> auth2EncryptedPrivateKey =
      new ConcurrentHashMap<Authentication, EncryptedPrivateKey>();

  @Override
  public synchronized boolean unlock(final Authentication authentication) {
    try {
      logger.debug("Unlock key with authentication: {}", authentication);
      final Authentication digested = digest(authentication);
      final EncryptedPrivateKey encrypted = auth2EncryptedPrivateKey.get(digested);
      if (null == encrypted) {
        throw new KeyStoreException("Invalid authentication");
      }

      final String origin = authentication.getPassword();
      current = new AergoKeyGenerator().create(encrypted, origin);
      currentAuth = digested;
      return true;
    } catch (Exception e) {
      logger.info("Unlock failed by {}", e.toString());
      return false;
    }
  }

  @Override
  public synchronized boolean lock(final Authentication authentication) {
    try {
      logger.debug("Unlock key with authentication: {}", authentication);
      final Authentication digested = digest(authentication);
      if (!currentAuth.equals(digested)) {
        throw new KeyStoreException("Invalid authentication");
      }

      current = null;
      currentAuth = null;
      return true;
    } catch (Exception e) {
      logger.info("Lock failed by {}", e.toString());
      return false;
    }
  }

  @Override
  public void save(final Authentication authentication, final AergoKey key) {
    try {
      logger.debug("Save key {} with authentication: {}", key, authentication);
      final EncryptedPrivateKey encryptedKey = key.export(authentication.getPassword());
      final Authentication digested = digest(authentication);
      auth2EncryptedPrivateKey.put(digested, encryptedKey);
    } catch (final Exception e) {
      throw new KeyStoreException(e);
    }
  }

  @Override
  public EncryptedPrivateKey export(final Authentication authentication) {
    try {
      logger.debug("Export key with authentication: {}", authentication);
      final Authentication digested = digest(authentication);
      final EncryptedPrivateKey encrypted = auth2EncryptedPrivateKey.get(digested);
      if (null == encrypted) {
        throw new KeyStoreException("Invalid authentication");
      }
      return encrypted;
    } catch (final KeyStoreException e) {
      throw e;
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
  public AccountAddress getPrincipal() {
    return null != current ? current.getPrincipal() : null;
  }

  @Override
  public Transaction sign(final RawTransaction rawTransaction) {
    logger.debug("Sign raw transaction: {}", rawTransaction);
    if (null == current) {
      throw new KeyStoreException("No unlocked account");
    }
    return current.sign(rawTransaction);
  }

  protected Authentication digest(final Authentication rawAuthentication) {
    final byte[] digestedPassword = Sha256Utils.digest(rawAuthentication.getPassword().getBytes());
    return Authentication.of(rawAuthentication.getIdentity(), new String(digestedPassword));
  }

}
