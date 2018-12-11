/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import hera.api.model.Authentication;
import hera.api.model.ClientManagedAccount;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.client.AergoClient;
import hera.exception.UnlockedAccountException;
import hera.exception.WalletException;
import hera.key.AergoKey;
import java.security.KeyStore;
import java.util.concurrent.atomic.AtomicBoolean;

public class NaiveWallet extends AbstractWallet {

  protected final AtomicBoolean unlocked = new AtomicBoolean(false);

  NaiveWallet(final AergoClient aergoClient, final int nonceRefreshCount) {
    super(aergoClient, nonceRefreshCount);
  }

  @Override
  public boolean unlock(final Authentication authentication) {
    unlocked.set(true);
    return true;
  }

  @Override
  public boolean lock(final Authentication authentication) {
    unlocked.set(false);
    return true;
  }

  @Override
  public Transaction sign(final RawTransaction rawTransaction) {
    if (false == unlocked.get()) {
      throw new UnlockedAccountException();
    }
    return aergoClient.getAccountOperation().sign(account, rawTransaction);
  }

  @Override
  public boolean verify(final Transaction transaction) {
    return aergoClient.getAccountOperation().verify(account, transaction);
  }

  @Override
  public void bindKeyStore(final KeyStore keyStore) {
    // do nothing
  }

  @Override
  public void saveKey(final AergoKey key, final String password) {
    this.account = ClientManagedAccount.of(key);
  }

  @Override
  public String exportKey(final Authentication authentication) {
    if (!getAddress().equals(authentication.getAddress())) {
      throw new WalletException("Invalid authentication");
    }
    return ((ClientManagedAccount) account).getKey()
        .export(authentication.getPassword()).getEncoded();
  }

}
