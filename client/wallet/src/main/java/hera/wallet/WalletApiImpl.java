/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import static hera.util.ValidationUtils.assertNotNull;

import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.BytesValue;
import hera.api.model.Hash;
import hera.api.model.RawTransaction;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.model.TryCountAndInterval;
import hera.api.transaction.NonceProvider;
import hera.api.transaction.SimpleNonceProvider;
import hera.client.AergoClient;
import hera.client.NonceRefreshingTxRequester;
import hera.client.TxRequester;
import hera.exception.HerajException;
import hera.exception.InvalidAuthenticationException;
import hera.key.Signer;
import hera.keystore.KeyStore;

class WalletApiImpl extends AbstractApi implements WalletApi, Signer {

  protected final KeyStore keyStore;
  protected final NonceProvider nonceProvider;  // to get nonce from a single base
  protected final TxRequester txRequester;

  protected final Object lock = new Object();
  protected final ProxySigner proxySigner = new ProxySigner();

  WalletApiImpl(final KeyStore keyStore, final TryCountAndInterval tryCountAndInterval) {
    assertNotNull(keyStore, "Keystore must not null");
    assertNotNull(tryCountAndInterval, "TryCountAndInterval must not null");
    this.keyStore = keyStore;
    this.nonceProvider = new SimpleNonceProvider();
    this.txRequester = new NonceRefreshingTxRequester(tryCountAndInterval, this.nonceProvider);
  }

  @Override
  public void bind(final AergoClient aergoClient) {
    throw new UnsupportedOperationException();
  }

  @Override
  public PreparedWalletApi with(final AergoClient aergoClient) {
    assertNotNull(aergoClient, "AergoClient must not null");
    return new PreparedWalletApiImpl(aergoClient, this.proxySigner, this.txRequester);
  }

  @Override
  public boolean unlock(final Authentication authentication) {
    try {
      assertNotNull(authentication, "Authentication must not null");
      logger.debug("Unlock with {}", authentication);

      synchronized (lock) {
        if (this.proxySigner.isUnlocked()) {
          throw new HerajException("Lock already unlocked one");
        }

        this.proxySigner.setUnlocked(keyStore.load(authentication));
      }
      return true;
    } catch (InvalidAuthenticationException e) {
      return false;
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public boolean lock(final Authentication authentication) {
    return lock();
  }

  @Override
  public boolean lock() {
    try {
      logger.debug("Lock wallet api");

      synchronized (lock) {
        if (!this.proxySigner.isUnlocked()) {
          return false;
        }

        this.proxySigner.lock();
      }
      return true;
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public TransactionApi transactionApi() {
    throw new UnsupportedOperationException();
  }

  @Override
  public QueryApi queryApi() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Transaction sign(final RawTransaction rawTransaction) {
    try {
      return this.proxySigner.sign(rawTransaction);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public Signature signMessage(final BytesValue message) {
    try {
      return this.proxySigner.signMessage(message);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public Signature signMessage(final Hash hashedMessage) {
    try {
      return this.proxySigner.signMessage(hashedMessage);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public AccountAddress getPrincipal() {
    return this.proxySigner.isUnlocked() ? this.proxySigner.getUnlocked().getPrincipal() : null;
  }

  @Override
  public String toString() {
    return String.format("WalletApiImpl(keyStore=%s, principal=%s)", keyStore.getClass().getName(),
        getPrincipal());
  }

}
