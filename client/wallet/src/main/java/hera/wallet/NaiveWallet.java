/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import hera.api.model.AccountFactory;
import hera.api.model.Authentication;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.model.internal.TryCountAndInterval;
import hera.client.AergoClient;
import hera.exception.UnlockedAccountException;
import hera.exception.WalletException;
import hera.key.AergoKey;
import java.security.KeyStore;
import java.util.concurrent.atomic.AtomicBoolean;

public class NaiveWallet extends AbstractWallet {

  protected final AtomicBoolean unlocked = new AtomicBoolean(false);

  NaiveWallet(final AergoClient aergoClient, final TryCountAndInterval tryCountAndInterval) {
    super(aergoClient, tryCountAndInterval);
  }

  @Override
  public boolean unlock(final Authentication authentication) {
    if (getAccount().getAddress().equals(authentication.getAddress())) {
      unlocked.set(true);
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean lock(final Authentication authentication) {
    if (getAccount().getAddress().equals(authentication.getAddress())) {
      unlocked.set(false);
      return true;
    } else {
      return false;
    }
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
    this.account = new AccountFactory().create(key);
    unlocked.set(false);
  }

  @Override
  public String exportKey(final Authentication authentication) {
    if (!getAddress().equals(authentication.getAddress())) {
      throw new WalletException("Invalid authentication");
    }
    return account.getKey().export(authentication.getPassword()).getEncoded();
  }

}
