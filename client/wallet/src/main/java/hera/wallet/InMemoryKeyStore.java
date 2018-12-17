/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountFactory;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.exception.LockedAccountException;
import hera.exception.WalletException;
import hera.key.AergoKey;
import hera.key.Signer;
import hera.util.Sha256Utils;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;

public class InMemoryKeyStore implements KeyStore, Signer {

  protected final Logger logger = getLogger(getClass());

  protected Map<Authentication, EncryptedPrivateKey> auth2EncryptedPrivateKey =
      new ConcurrentHashMap<>();

  protected Set<Authentication> unlockedAuthSet = ConcurrentHashMap.newKeySet();

  protected Map<AccountAddress, AergoKey> address2Unlocked = new ConcurrentHashMap<>();

  @Override
  public void save(final AergoKey key, final String password) {
    try {
      auth2EncryptedPrivateKey.put(digest(Authentication.of(key.getAddress(), password)),
          key.export(password));
    } catch (final Exception e) {
      throw new WalletException(e);
    }
  }

  @Override
  public EncryptedPrivateKey export(final Authentication rawAuthentication) {
    try {
      final EncryptedPrivateKey exported =
          auth2EncryptedPrivateKey.get(digest(rawAuthentication));
      if (null == exported) {
        throw new WalletException("Authentication failure");
      }
      return exported;
    } catch (final Exception e) {
      throw new WalletException(e);
    }
  }

  @Override
  public Account unlock(final Authentication authentication) {
    try {
      Account loaded = null;
      final Authentication digestedAuth = digest(authentication);
      final EncryptedPrivateKey encrypted =
          auth2EncryptedPrivateKey.get(digestedAuth);
      if (null != encrypted) {
        unlockedAuthSet.add(digestedAuth);
        final AergoKey key = AergoKey.of(encrypted, authentication.getPassword());
        address2Unlocked.put(key.getAddress(), key);
        return new AccountFactory().create(key.getAddress(), this);
      }
      return loaded;
    } catch (Exception e) {
      logger.info("Key loading failure : {}", e.getMessage());
      return null;
    }
  }

  @Override
  public boolean lock(final Authentication authentication) {
    if (true == unlockedAuthSet.remove(digest(authentication))) {
      return null != address2Unlocked.remove(authentication.getAddress());
    }
    return false;
  }

  protected Authentication digest(final Authentication rawAuthentication) {
    final byte[] digestedPassword = Sha256Utils.digest(rawAuthentication.getPassword().getBytes());
    return Authentication.of(rawAuthentication.getAddress(), new String(digestedPassword));
  }

  @Override
  public Transaction sign(final RawTransaction rawTransaction) {
    final AccountAddress sender = rawTransaction.getSender();
    if (null == sender) {
      throw new WalletException("Sender is null");
    }

    final AergoKey key = getUnlockedKey(sender);
    try {
      return key.sign(rawTransaction);
    } catch (Exception e) {
      throw new WalletException(e);
    }
  }

  @Override
  public boolean verify(final Transaction transaction) {
    final AccountAddress sender = transaction.getSender();
    if (null == sender) {
      throw new WalletException("Sender is null");
    }

    final AergoKey key = getUnlockedKey(sender);
    try {
      return key.verify(transaction);
    } catch (Exception e) {
      throw new WalletException(e);
    }
  }

  protected AergoKey getUnlockedKey(final AccountAddress address) {
    final AergoKey key = address2Unlocked.get(address);
    if (null == key) {
      throw new LockedAccountException();
    }
    return key;
  }

}
