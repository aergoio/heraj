/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.key.AergoKey;

public interface KeyStoreAdaptor {

  public void save(AergoKey key, String password);

  public EncryptedPrivateKey export(Authentication authentication);

  public AccountAddress unlock(Authentication authentication);

  public boolean lock(Authentication authentication);

  public Transaction sign(AccountAddress signerAddress, RawTransaction rawTransaction);

  public boolean verify(AccountAddress signerAddress, Transaction transaction);

}
