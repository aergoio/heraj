/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore;

import static java.util.Collections.newSetFromMap;
import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;

@ApiAudience.Private
@ApiStability.Unstable
public class InMemoryKeyStore implements KeyStore, Signer {

  protected final Logger logger = getLogger(getClass());

  protected Map<Authentication, EncryptedPrivateKey> auth2EncryptedPrivateKey =
      new ConcurrentHashMap<Authentication, EncryptedPrivateKey>();

  protected Set<AccountAddress> storedAddressSet =
      newSetFromMap(new ConcurrentHashMap<AccountAddress, Boolean>());

  protected Authentication currentUnlockedAuth;
  protected AergoKey currentUnlockedKey;

  @Override
  public void saveKey(final AergoKey key, final String password) {
    try {
      auth2EncryptedPrivateKey.put(digest(Authentication.of(key.getAddress(), password)),
          key.export(password));
      storedAddressSet.add(key.getAddress());
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
  public List<AccountAddress> listStoredAddresses() {
    return new ArrayList<AccountAddress>(this.storedAddressSet);
  }

  @Override
  public Account unlock(final Authentication authentication) {
    try {
      final Authentication digestedAuth = digest(authentication);
      if (false == auth2EncryptedPrivateKey.containsKey(digestedAuth)) {
        throw new InvalidAuthentiationException("A key mappged with Authentication isn't exist");
      }
      final EncryptedPrivateKey encrypted =
          auth2EncryptedPrivateKey.get(digestedAuth);
      final AergoKey key = AergoKey.of(encrypted, authentication.getPassword());
      currentUnlockedAuth = digestedAuth;
      currentUnlockedKey = key;
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
    if (!currentUnlockedAuth.equals(digested)) {
      throw new InvalidAuthentiationException("Unable to lock account");
    }
    currentUnlockedAuth = null;
    currentUnlockedKey = null;
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
    if (null == currentUnlockedKey || !currentUnlockedKey.getAddress().equals(address)) {
      throw new LockedAccountException();
    }
    return currentUnlockedKey;
  }

  @Override
  public void store(final String path, final String password) {
    // do nothing
  }

}
