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
import hera.exception.WalletExceptionConverter;
import hera.key.Signer;
import hera.keystore.KeyStore;
import hera.model.TryCountAndInterval;
import hera.util.ExceptionConverter;
import hera.util.Sha256Utils;
import hera.wallet.internal.ClientInjectable;
import hera.wallet.internal.QueryApiImpl;
import hera.wallet.internal.TransactionApiImpl;
import lombok.Getter;

public class WalletApiImpl implements WalletApi {

  protected final ExceptionConverter<HerajException> converter = new WalletExceptionConverter();

  protected final KeyStore keyStore;

  protected final TransactionApi transactionApi;

  protected final QueryApi queryApi = new QueryApiImpl();

  protected AergoClient aergoClient = null;

  protected Signer signer = null;
  protected String authMac = null;
  @Getter
  protected AccountAddress principal = null;

  WalletApiImpl(final KeyStore keyStore, final TryCountAndInterval tryCountAndInterval) {
    assertNotNull(keyStore);
    assertNotNull(tryCountAndInterval);
    this.keyStore = keyStore;
    this.transactionApi = new TransactionApiImpl(tryCountAndInterval, this);
  }

  @Override
  public void bind(final AergoClient aergoClient) {
    assertNotNull(aergoClient);
    this.aergoClient = aergoClient;
    if (transactionApi instanceof ClientInjectable) {
      ((ClientInjectable) transactionApi).setClient(aergoClient);
    }
    if (queryApi instanceof ClientInjectable) {
      ((ClientInjectable) queryApi).setClient(aergoClient);
    }
  }

  @Override
  public synchronized boolean unlock(final Authentication authentication) {
    try {
      assertNotNull(authentication, "Authentication must not null");
      this.signer = keyStore.load(authentication);
      this.authMac = digest(authentication);
      this.principal = signer.getPrincipal();
      return true;
    } catch (InvalidAuthenticationException e) {
      return false;
    } catch (Exception e) {
      throw converter.convert(e);
    }
  }

  @Override
  public synchronized boolean lock(final Authentication authentication) {
    try {
      assertNotNull(authentication, "Authentication must not null");
      final String digested = digest(authentication);
      if (null == this.authMac || false == this.authMac.equals(digested)) {
        return false;
      }

      this.signer = null;
      this.authMac = null;
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
    if (null == this.aergoClient) {
      throw new HerajException("Bind client first by 'WalletApi.bind(aergoClient)'");
    }
    return this.transactionApi;
  }

  @Override
  public QueryApi queryApi() {
    if (null == this.aergoClient) {
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
    final AccountAddress current = getPrincipal();
    return String.format("WalletApi(keyStore=%s, pricipal=%s)", keyStore.getClass().getName(),
        null == current ? null : current.getEncoded());
  }

  protected Signer getSigner() {
    if (null == this.signer) {
      throw new HerajException("Unlock account first");
    }
    return this.signer;
  }

}
