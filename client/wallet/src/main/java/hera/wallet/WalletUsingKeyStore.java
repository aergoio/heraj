/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.RawTransaction;
import hera.api.model.ServerManagedAccount;
import hera.api.model.Transaction;
import hera.client.AergoClient;
import hera.key.AergoKey;

public abstract class WalletUsingKeyStore extends AbstractWallet {

  protected KeyStoreAdaptor keyStore;

  protected WalletUsingKeyStore(final AergoClient aergoClient, final int nonceRefreshCount) {
    super(aergoClient, nonceRefreshCount);
  }

  @Override
  public boolean unlock(final Authentication authentication) {
    final AccountAddress unlockedAccount = keyStore.unlock(authentication);
    if (null != unlockedAccount) {
      this.account = ServerManagedAccount.of(unlockedAccount);
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean lock(final Authentication authentication) {
    return keyStore.lock(authentication);
  }

  @Override
  public Transaction sign(final RawTransaction rawTransaction) {
    return keyStore.sign(getAccount().getAddress(), rawTransaction);
  }

  @Override
  public boolean verify(final Transaction transaction) {
    return keyStore.verify(getAccount().getAddress(), transaction);
  }

  @Override
  public void saveKey(final AergoKey key, final String password) {
    keyStore.save(key, password);
  }

  @Override
  public String exportKey(final Authentication authentication) {
    return keyStore.export(authentication).getEncoded();
  }

}
