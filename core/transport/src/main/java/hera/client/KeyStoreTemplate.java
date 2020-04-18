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

class KeyStoreTemplate extends AbstractTemplate implements KeyStoreOperation {

  protected final KeyStoreMethods keyStoreMethods = new KeyStoreMethods();

  KeyStoreTemplate(final ContextStorage<Context> contextStorage) {
    super(contextStorage);
  }

  @Override
  public List<AccountAddress> list() {
    return request(keyStoreMethods.getList());
  }

  @Override
  public AccountAddress create(final String password) {
    return request(keyStoreMethods.getCreate(), Arrays.<Object>asList(password));
  }

  @Override
  public boolean lock(final Authentication authentication) {
    return request(keyStoreMethods.getLock(), Arrays.<Object>asList(authentication));
  }

  @Override
  public boolean unlock(final Authentication authentication) {
    return request(keyStoreMethods.getUnlock(), Arrays.<Object>asList(authentication));
  }

  @Override
  public Transaction sign(final RawTransaction rawTransaction) {
    return request(keyStoreMethods.getSign(), Arrays.<Object>asList(rawTransaction));
  }

  @Override
  public AccountAddress importKey(final EncryptedPrivateKey encryptedKey, final String oldPassword,
      final String newPassword) {
    return request(keyStoreMethods.getImportKey(),
        Arrays.asList(encryptedKey, oldPassword, newPassword));
  }

  @Override
  public EncryptedPrivateKey exportKey(final Authentication authentication) {
    return request(keyStoreMethods.getExportKey(), Arrays.<Object>asList(authentication));
  }

  @Override
  public TxHash send(final AccountAddress sender, final AccountAddress recipient, final Aer amount,
      final BytesValue payload) {
    return request(keyStoreMethods.getSend(),
        Arrays.asList(sender, recipient, amount, payload));
  }

}

