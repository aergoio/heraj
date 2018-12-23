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
import hera.exception.InvalidAuthentiationException;
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
  public void saveKey(final AergoKey key, final String password) {
    try {
      auth2EncryptedPrivateKey.put(digest(Authentication.of(key.getAddress(), password)),
          key.export(password));
    } catch (final Exception e) {
      throw new WalletException(e);
    }
  }

  @Override
  public EncryptedPrivateKey export(final Authentication authentication) {
    try {
      final Authentication digestedAuth = digest(authentication);
      if (false == auth2EncryptedPrivateKey.containsKey(digestedAuth)) {
        throw new InvalidAuthentiationException("A key mappged with Authentication isn't exist");
      }
      return auth2EncryptedPrivateKey.get(digestedAuth);
    } catch (final Exception e) {
      throw new InvalidAuthentiationException(e);
    }
  }

  @Override
  public Account unlock(final Authentication authentication) {
    try {
      final Authentication digestedAuth = digest(authentication);
      if (false == auth2EncryptedPrivateKey.containsKey(digestedAuth)) {
        throw new InvalidAuthentiationException("A key mappged with Authentication isn't exist");
      }
      unlockedAuthSet.add(digestedAuth);
      final EncryptedPrivateKey encrypted =
          auth2EncryptedPrivateKey.get(digestedAuth);
      final AergoKey key = AergoKey.of(encrypted, authentication.getPassword());
      address2Unlocked.put(key.getAddress(), key);
      return new AccountFactory().create(key.getAddress(), this);
    } catch (WalletException e) {
      throw e;
    } catch (Exception e) {
      throw new InvalidAuthentiationException(e);
    }
  }

  @Override
  public void lock(final Authentication authentication) {
    final Authentication digested = digest(authentication);
    if (false == unlockedAuthSet.contains(digested)) {
      throw new InvalidAuthentiationException("Unable to lock account");
    }
    unlockedAuthSet.remove(digested);
    address2Unlocked.remove(authentication.getAddress());
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

  @Override
  public void store(final String path, final String password) {
    // do nothing
  }

}
