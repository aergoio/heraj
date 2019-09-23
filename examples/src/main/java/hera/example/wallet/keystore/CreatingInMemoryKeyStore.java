/*
 * @copyright defined in LICENSE.txt
 */

package hera.example.wallet.keystore;

import hera.example.AbstractExample;
import hera.keystore.InMemoryKeyStore;
import hera.keystore.KeyStore;
import hera.wallet.WalletApi;
import hera.wallet.WalletFactory;

public class CreatingInMemoryKeyStore extends AbstractExample {

  @Override
  public void run() throws Exception {
    // create in memory keystore
    KeyStore keyStore = new InMemoryKeyStore();
    System.out.println("Keystore: " + keyStore);

    // create walletapi
    WalletApi walletApi = new WalletFactory().create(keyStore);
    System.out.println("Walletapi with keystore: " + walletApi);
  }

  public static void main(String[] args) throws Exception {
    new CreatingInMemoryKeyStore().run();
  }

}
