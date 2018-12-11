/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.RawTransaction;
import hera.api.model.ServerManagedAccount;
import hera.api.model.Transaction;
import hera.client.AergoClient;
import hera.key.AergoKey;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServerKeyStoreAdaptor implements KeyStoreAdaptor {

  protected final AergoClient aergoClient;

  @Override
  public void save(final AergoKey key, final String password) {
    aergoClient.getKeyStoreOperation().importKey(key.export(password), password);
  }

  @Override
  public EncryptedPrivateKey export(final Authentication authentication) {
    return aergoClient.getKeyStoreOperation().exportKey(authentication);
  }

  @Override
  public AccountAddress unlock(final Authentication authentication) {
    try {
      final boolean unlocked = aergoClient.getKeyStoreOperation().unlock(authentication);
      return unlocked ? authentication.getAddress() : null;
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public boolean lock(final Authentication authentication) {
    return aergoClient.getKeyStoreOperation().lock(authentication);
  }

  @Override
  public Transaction sign(AccountAddress signerAddress, RawTransaction rawTransaction) {
    return aergoClient.getAccountOperation().sign(ServerManagedAccount.of(signerAddress),
        rawTransaction);
  }

  @Override
  public boolean verify(AccountAddress signerAddress, Transaction transaction) {
    return aergoClient.getAccountOperation().verify(ServerManagedAccount.of(signerAddress),
        transaction);
  }

}
