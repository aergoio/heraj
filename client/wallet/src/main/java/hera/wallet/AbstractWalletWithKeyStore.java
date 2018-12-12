/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.RawTransaction;
import hera.api.model.ServerManagedAccount;
import hera.api.model.Transaction;
import hera.api.model.TryCountAndInterval;
import hera.client.AergoClient;
import hera.key.AergoKey;
import lombok.AccessLevel;
import lombok.Getter;

public abstract class AbstractWalletWithKeyStore extends AbstractWallet {

  @Getter(value = AccessLevel.PROTECTED)
  protected KeyStoreAdaptor keyStore;

  protected AbstractWalletWithKeyStore(final AergoClient aergoClient,
      final TryCountAndInterval nonceRefreshTryCount) {
    super(aergoClient, nonceRefreshTryCount);
  }

  @Override
  public boolean unlock(final Authentication authentication) {
    final AccountAddress unlockedAccount = getKeyStore().unlock(authentication);
    if (null != unlockedAccount) {
      this.account = ServerManagedAccount.of(unlockedAccount);
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean lock(final Authentication authentication) {
    return getKeyStore().lock(authentication);
  }

  @Override
  public Transaction sign(final RawTransaction rawTransaction) {
    return getKeyStore().sign(getAccount().getAddress(), rawTransaction);
  }

  @Override
  public boolean verify(final Transaction transaction) {
    return getKeyStore().verify(getAccount().getAddress(), transaction);
  }

  @Override
  public void saveKey(final AergoKey key, final String password) {
    getKeyStore().save(key, password);
  }

  @Override
  public String exportKey(final Authentication authentication) {
    return getKeyStore().export(authentication).getEncoded();
  }

}
