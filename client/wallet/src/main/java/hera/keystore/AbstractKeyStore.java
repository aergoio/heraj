/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore;

import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.exception.KeyStoreException;
import hera.key.AergoKey;
import hera.key.Signer;
import hera.util.Sha256Utils;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;

@ApiAudience.Private
@ApiStability.Unstable
public abstract class AbstractKeyStore implements KeyStore {

  protected final transient Logger logger = getLogger(getClass());

  // auth -> unlocked -> signer
  protected final Map<Authentication, AccountAddress> auth2Unlocked = new HashMap<>();
  protected final Map<AccountAddress, Signer> unlocked2Signer = new HashMap<>();

  @Override
  public synchronized AccountAddress unlock(final Authentication authentication) {
    try {
      logger.debug("Unlock key with authentication: {}", authentication);
      final Authentication digested = digest(authentication);
      if (auth2Unlocked.containsKey(digested)) {
        return auth2Unlocked.get(digested);
      }

      final AergoKey unlockedKey = getUnlockedOne(authentication);
      final AccountAddress unlocked = unlockedKey.getAddress();
      auth2Unlocked.put(digested, unlocked);
      unlocked2Signer.put(unlocked, unlockedKey);
      return unlockedKey.getPrincipal();
    } catch (KeyStoreException e) {
      throw e;
    } catch (Exception e) {
      throw new KeyStoreException("Unlock failed", e);
    }
  }

  protected abstract AergoKey getUnlockedOne(Authentication authentication);

  @Override
  public synchronized boolean lock(final Authentication authentication) {
    try {
      logger.debug("Unlock key with authentication: {}", authentication);
      final Authentication digested = digest(authentication);
      if (!auth2Unlocked.containsKey(digested)) {
        logger.info("No unlocked one for authentication");
        return false;
      }

      final AccountAddress unlocked = auth2Unlocked.get(digested);
      auth2Unlocked.remove(digested);
      unlocked2Signer.remove(unlocked);
      return true;
    } catch (Exception e) {
      logger.info("Lock failed by {}", e.toString());
      return false;
    }
  }

  protected Authentication digest(final Authentication rawAuthentication) {
    final byte[] digestedPassword = Sha256Utils.digest(rawAuthentication.getPassword().getBytes());
    return Authentication.of(rawAuthentication.getIdentity(), new String(digestedPassword));
  }

  @Override
  public Transaction sign(final AccountAddress unlocked,
      final RawTransaction rawTransaction) {
    try {
      logger.debug("Sign with unlocked account: {} to raw transaction: {}", unlocked,
          rawTransaction);
      if (!unlocked2Signer.containsKey(unlocked)) {
        throw new IllegalArgumentException("No unlocked account for " + unlocked);
      }

      final Signer signer = unlocked2Signer.get(unlocked);
      return signer.sign(rawTransaction);
    } catch (Exception e) {
      throw new KeyStoreException(e);
    }
  }

}
