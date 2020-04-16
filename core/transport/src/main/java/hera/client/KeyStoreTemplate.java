/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.Context;
import hera.ContextStorage;
import hera.api.KeyStoreOperation;
import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.Authentication;
import hera.api.model.BytesValue;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

class KeyStoreTemplate extends AbstractTemplate implements KeyStoreOperation {

  protected final KeyStoreMethods keyStoreMethods = new KeyStoreMethods();

  KeyStoreTemplate(final ContextStorage<Context> contextStorage) {
    super(contextStorage);
  }

  @Override
  public List<AccountAddress> list() {
    return request(new Callable<List<AccountAddress>>() {
      @Override
      public List<AccountAddress> call() throws Exception {
        return requester.request(keyStoreMethods
            .getList()
            .toInvocation());
      }
    });
  }

  @Override
  public AccountAddress create(final String password) {
    return request(new Callable<AccountAddress>() {
      @Override
      public AccountAddress call() throws Exception {
        return requester.request(keyStoreMethods
            .getCreate()
            .toInvocation(Arrays.<Object>asList(password)));
      }
    });
  }

  @Override
  public boolean lock(final Authentication authentication) {
    return request(new Callable<Boolean>() {
      @Override
      public Boolean call() throws Exception {
        return requester.request(keyStoreMethods
            .lock
            .toInvocation(Arrays.<Object>asList(authentication)));
      }
    });
  }

  @Override
  public boolean unlock(final Authentication authentication) {
    return request(new Callable<Boolean>() {
      @Override
      public Boolean call() throws Exception {
        return requester.request(keyStoreMethods
            .getUnlock()
            .toInvocation(Arrays.<Object>asList(authentication)));
      }
    });
  }

  @Override
  public Transaction sign(final RawTransaction rawTransaction) {
    return request(new Callable<Transaction>() {
      @Override
      public Transaction call() throws Exception {
        return requester.request(keyStoreMethods
            .getSign()
            .toInvocation(Arrays.<Object>asList(rawTransaction)));
      }
    });
  }

  @Override
  public AccountAddress importKey(final EncryptedPrivateKey encryptedKey, final String oldPassword,
      final String newPassword) {
    return request(new Callable<AccountAddress>() {
      @Override
      public AccountAddress call() throws Exception {
        return requester.request(keyStoreMethods
            .getImportKey()
            .toInvocation(Arrays.asList(encryptedKey, oldPassword, newPassword)));
      }
    });
  }

  @Override
  public EncryptedPrivateKey exportKey(final Authentication authentication) {
    return request(new Callable<EncryptedPrivateKey>() {
      @Override
      public EncryptedPrivateKey call() throws Exception {
        return requester.request(keyStoreMethods
            .getExportKey()
            .toInvocation(Arrays.<Object>asList(authentication)));
      }
    });
  }

  @Override
  public TxHash send(final AccountAddress sender, final AccountAddress recipient, final Aer amount,
      final BytesValue payload) {
    return request(new Callable<TxHash>() {
      @Override
      public TxHash call() throws Exception {
        return requester.request(keyStoreMethods
            .getSend()
            .toInvocation(Arrays.<Object>asList(sender, recipient, amount, payload)));
      }
    });
  }

}

