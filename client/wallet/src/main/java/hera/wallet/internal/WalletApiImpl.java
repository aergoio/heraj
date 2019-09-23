/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet.internal;

import static hera.util.ValidationUtils.assertNotNull;

import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.model.internal.TryCountAndInterval;
import hera.client.AergoClient;
import hera.exception.WalletException;
import hera.exception.WalletExceptionConverter;
import hera.keystore.KeyStore;
import hera.util.ExceptionConverter;
import hera.wallet.QueryApi;
import hera.wallet.TransactionApi;
import hera.wallet.WalletApi;
import lombok.Getter;

public class WalletApiImpl implements WalletApi {

  protected final ExceptionConverter<WalletException> converter = new WalletExceptionConverter();

  protected final KeyStore keyStore;

  protected final TransactionApi transactionApi;

  protected final QueryApi queryApi = new QueryApiImpl();

  @Getter
  protected AccountAddress principal = null;

  public WalletApiImpl(final TryCountAndInterval tryCountAndInterval, final KeyStore keyStore) {
    this.keyStore = keyStore;
    this.transactionApi = new TransactionApiImpl(tryCountAndInterval, this);
  }

  @Override
  public void bind(final AergoClient client) {
    assertNotNull(client);
    if (transactionApi instanceof ClientInjectable) {
      ((ClientInjectable) transactionApi).setClient(client);
    }
    if (queryApi instanceof ClientInjectable) {
      ((ClientInjectable) queryApi).setClient(client);
    }
  }

  @Override
  public synchronized boolean unlock(final Authentication authentication) {
    try {
      this.principal = keyStore.unlock(authentication);
      return true;
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public synchronized boolean lock(final Authentication authentication) {
    try {
      final boolean lockResult = keyStore.lock(authentication);
      return lockResult;
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
  public Transaction sign(final RawTransaction rawTransaction) {
    try {
      if (!getPrincipal().equals(rawTransaction.getSender())) {
        throw new WalletException("Sender of the rawTransaction should equals with unlocked one");
      }
      return keyStore.sign(getPrincipal(), rawTransaction);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public String toString() {
    return String.format("WalletApi(keyStore=%s)", keyStore.getClass().getName());
  }

}
