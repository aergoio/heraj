/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet.internal;

import static hera.util.ValidationUtils.assertNotNull;

import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.Identity;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.model.internal.TryCountAndInterval;
import hera.client.AergoClient;
import hera.exception.WalletException;
import hera.exception.WalletExceptionConverter;
import hera.key.AergoKey;
import hera.keystore.JavaKeyStoreInjectable;
import hera.keystore.KeyStore;
import hera.util.ExceptionConverter;
import hera.wallet.QueryApi;
import hera.wallet.TransactionApi;
import hera.wallet.WalletApi;
import java.util.List;

public class WalletApiImpl implements WalletApi {

  protected final ExceptionConverter<WalletException> converter = new WalletExceptionConverter();

  protected final KeyStore keyStore;

  protected final TransactionApi transactionApi;

  protected final QueryApi queryApi = new QueryApiImpl();

  public WalletApiImpl(final TryCountAndInterval tryCountAndInterval, final KeyStore keyStore) {
    this.keyStore = keyStore;
    this.transactionApi = new TransactionApiImpl(tryCountAndInterval, this);
  }

  @Override
  public void use(final AergoClient client) {
    assertNotNull(client);
    if (keyStore instanceof ClientInjectable) {
      ((ClientInjectable) keyStore).setClient(client);
    }
    if (transactionApi instanceof ClientInjectable) {
      ((ClientInjectable) transactionApi).setClient(client);
    }
    if (queryApi instanceof ClientInjectable) {
      ((ClientInjectable) queryApi).setClient(client);
    }
  }

  @Override
  public void bind(final java.security.KeyStore keyStore) {
    assertNotNull(keyStore);
    if (this.keyStore instanceof JavaKeyStoreInjectable) {
      ((JavaKeyStoreInjectable) this.keyStore).setJavaKeyStore(keyStore);
    }
  }

  @Override
  public boolean unlock(Authentication authentication) {
    try {
      return keyStore.unlock(authentication);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public boolean lock(Authentication authentication) {
    try {
      return keyStore.lock(authentication);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public void save(Authentication authentication, AergoKey key) {
    try {
      keyStore.save(authentication, key);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public String export(Authentication authentication) {
    try {
      return keyStore.export(authentication).getEncoded();
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public List<Identity> listIdentities() {
    try {
      return keyStore.listIdentities();
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public TransactionApi transactionApi() {
    return this.transactionApi;
  }

  @Override
  public QueryApi queryApi() {
    return this.queryApi;
  }

  @Override
  public AccountAddress getPrincipal() {
    return keyStore.getPrincipal();
  }

  @Override
  public Transaction sign(final RawTransaction rawTransaction) {
    try {
      if (!getPrincipal().equals(rawTransaction.getSender())) {
        throw new WalletException("Sender of the rawTransaction should equals with unlocked one");
      }
      return keyStore.sign(rawTransaction);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

}
