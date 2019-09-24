/*
 * @copyright defined in LICENSE.txt
 */

package hera.example.wallet.keystore;

import hera.example.AbstractExample;
import hera.keystore.JavaKeyStore;
import hera.keystore.KeyStore;
import hera.wallet.WalletApi;
import hera.wallet.WalletFactory;

public class CreatingJavaKeyStore extends AbstractExample {

  @Override
  public void run() throws Exception {
    // create new java key store with type PKCS12
    KeyStore created = new JavaKeyStore("PKCS12");
    System.out.println("Created keystore: " + created);

    // create walletapi with created one
    WalletApi withCreatedOne = new WalletFactory().create(created);
    System.out.println("Walletapi with created keystore: " + withCreatedOne);

    // save key store to the path
    String path = System.getProperty("java.io.tmpdir") + ".tmpkeystore";
    char[] password = "password".toCharArray();
    created.store(path, password);;

    // create new java key store with type PKCS12
    KeyStore loaded = new JavaKeyStore("PKCS12", path, password);
    System.out.println("Loaded keystore: " + loaded);

    // create walletapi with loaded one
    WalletApi withLoadedOne = new WalletFactory().create(created);
    System.out.println("Walletapi with loaded keystore: " + withLoadedOne);
  }

  public static void main(String[] args) throws Exception {
    new CreatingJavaKeyStore().run();
  }

}
