/*
 * @copyright defined in LICENSE.txt
 */

package hera.example.wallet.keystore;

import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.Identity;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.key.AergoKey;
import hera.keystore.KeyStore;
import java.util.List;

public class CustomKeyStore implements KeyStore {

  @Override
  public AccountAddress unlock(Authentication authentication) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean lock(Authentication authentication) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void save(Authentication authentication, AergoKey key) {
    // TODO Auto-generated method stub

  }

  @Override
  public EncryptedPrivateKey export(Authentication authentication) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Identity> listIdentities() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void store(String path, char[] password) {
    // TODO Auto-generated method stub

  }

  @Override
  public Transaction sign(AccountAddress unlocked, RawTransaction rawTransaction) {
    // TODO Auto-generated method stub
    return null;
  }

}
