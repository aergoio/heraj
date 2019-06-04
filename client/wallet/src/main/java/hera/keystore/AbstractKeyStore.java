/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.exception.KeyStoreException;
import hera.key.AergoKey;
import hera.transaction.TxSigner;
import hera.util.Sha256Utils;
import org.slf4j.Logger;

public abstract class AbstractKeyStore implements KeyStore, TxSigner {

  protected final transient Logger logger = getLogger(getClass());

  protected Authentication currentlyUnlockedAuth;

  protected AergoKey currentlyUnlockedKey;

  @Override
  public synchronized boolean lock(final Authentication authentication) {
    try {
      logger.debug("Lock key with authentication: {}", authentication);
      final Authentication digested = digest(authentication);
      if (!currentlyUnlockedAuth.equals(digested)) {
        return false;
      }
      currentlyUnlockedAuth = null;
      currentlyUnlockedKey = null;
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

  protected AergoKey getUnlockedKey() {
    if (null == currentlyUnlockedKey) {
      throw new IllegalStateException("Unlock account first");
    }
    return currentlyUnlockedKey;
  }

}
