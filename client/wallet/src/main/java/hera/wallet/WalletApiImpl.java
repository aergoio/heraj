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
import hera.client.AergoClient;
import hera.exception.HerajException;
import hera.exception.InvalidAuthenticationException;
import hera.key.Signer;
import hera.keystore.KeyStore;
import hera.util.Sha256Utils;

class WalletApiImpl extends AbstractApi implements WalletApi, Signer, ClientProvider {

  protected final KeyStore keyStore;

  protected final TransactionApi transactionApi;

  protected final QueryApi queryApi;

  protected volatile AergoClient aergoClient;

  protected final Object lock = new Object();
  protected Signer delegate;
  protected String authMac;

  WalletApiImpl(final KeyStore keyStore, final TryCountAndInterval tryCountAndInterval) {
    assertNotNull(keyStore, "Keystore must not null");
    assertNotNull(tryCountAndInterval, "TryCountAndInterval must not null");
    this.keyStore = keyStore;
    final TxRequester txRequester = new NonceRefreshingTxRequester(this,
        tryCountAndInterval);
    this.transactionApi = new TransactionApiImpl(this, this, txRequester);
    this.queryApi = new QueryApiImpl(this);
  }

  @Override
  public void bind(final AergoClient aergoClient) {
    assertNotNull(aergoClient, "AergoClient must not null");
    this.aergoClient = aergoClient;
  }

  @Override
  public AergoClient getClient() {
    return this.aergoClient;
  }

  @Override
  public boolean unlock(final Authentication authentication) {
    try {
      assertNotNull(authentication, "Authentication must not null");
      logger.debug("Unlock with {}", authentication);

      synchronized (lock) {
        if (null != this.authMac) {
          throw new HerajException("Lock already unlocked one");
        }

        this.delegate = keyStore.load(authentication);
        this.authMac = digest(authentication);
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
    try {
      assertNotNull(authentication, "Authentication must not null");
      logger.debug("Lock with {}", authentication);

      final String digested = digest(authentication);
      synchronized (lock) {
        if (!digested.equals(this.authMac)) {
          return false;
        }

        this.delegate = null;
        this.authMac = null;
      }
      return true;
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  protected String digest(final Authentication authentication) {
    final byte[] rawIdentity = authentication.getIdentity().getValue().getBytes();
    final byte[] rawPassword = authentication.getPassword().getBytes();
    final byte[] plaintext = new byte[rawIdentity.length + rawPassword.length];
    System.arraycopy(rawIdentity, 0, plaintext, 0, rawIdentity.length);
    System.arraycopy(rawPassword, 0, plaintext, rawIdentity.length, rawPassword.length);
    return new String(Sha256Utils.digest(plaintext));
  }

  @Override
  public TransactionApi transactionApi() {
    if (null == getClient()) {
      throw new HerajException("Bind client first by 'WalletApi.bind(aergoClient)'");
    }
    return this.transactionApi;
  }

  @Override
  public QueryApi queryApi() {
    if (null == getClient()) {
      throw new HerajException("Bind client first by 'WalletApi.bind(aergoClient)'");
    }
    return this.queryApi;
  }

  @Override
  public Transaction sign(final RawTransaction rawTransaction) {
    try {
      assertNotNull(rawTransaction, "Raw transaction must not null");
      if (!getPrincipal().equals(rawTransaction.getSender())) {
        throw new HerajException("Sender of the rawTransaction should equals with unlocked one");
      }
      return getSigner().sign(rawTransaction);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public Signature signMessage(final BytesValue message) {
    try {
      assertNotNull(message, "Message must not null");
      return getSigner().signMessage(message);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public Signature signMessage(final Hash hashedMessage) {
    try {
      assertNotNull(hashedMessage, "Hashed message must not null");
      return getSigner().signMessage(hashedMessage);
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public String toString() {
    return String.format("WalletApi(keyStore=%s, principal=%s)", keyStore.getClass().getName(),
        null != this.delegate ? this.delegate.getPrincipal() : null);
  }

  @Override
  public AccountAddress getPrincipal() {
    if (null == this.delegate) {
      throw new HerajException("Unlock account first");
    }
    return getSigner().getPrincipal();
  }

  protected Signer getSigner() {
    if (null == this.delegate) {
      throw new HerajException("Unlock account first");
    }
    return this.delegate;
  }

}
