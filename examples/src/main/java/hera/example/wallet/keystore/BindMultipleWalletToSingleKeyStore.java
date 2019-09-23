/*
 * @copyright defined in LICENSE.txt
 */

package hera.example.wallet.keystore;

import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.example.AbstractExample;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.keystore.InMemoryKeyStore;
import hera.keystore.KeyStore;
import hera.wallet.WalletApi;
import hera.wallet.WalletFactory;

public class BindMultipleWalletToSingleKeyStore extends AbstractExample {

  @Override
  public void run() throws Exception {
    // create in memory keystore
    KeyStore keyStore = new InMemoryKeyStore();

    // create key1, key2 and store it
    AergoKey key1 = new AergoKeyGenerator().create();
    Authentication authentication1 = Authentication.of(key1.getAddress(), "password");
    keyStore.save(authentication1, key1);

    AergoKey key2 = new AergoKeyGenerator().create();
    Authentication authentication2 = Authentication.of(key2.getAddress(), "password");
    keyStore.save(authentication2, key2);

    System.out.println("Key1: " + key1);
    System.out.println("Key2: " + key2);

    // create walletapis
    WalletApi walletApi1 = new WalletFactory().create(keyStore);
    WalletApi walletApi2 = new WalletFactory().create(keyStore);

    // unlock and bind account to each wallet api
    walletApi1.unlock(authentication1);
    walletApi2.unlock(authentication2);
    
    // check binded one
    AccountAddress unlocked1 = walletApi1.getPrincipal();
    AccountAddress unlocked2 = walletApi2.getPrincipal();
    System.out.println("Unlocked1: " + unlocked1);
    System.out.println("Unlocked2: " + unlocked2);
  }

  public static void main(String[] args) throws Exception {
    new BindMultipleWalletToSingleKeyStore().run();
  }

}
