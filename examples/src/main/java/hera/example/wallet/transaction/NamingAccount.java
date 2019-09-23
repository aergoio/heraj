/*
 * @copyright defined in LICENSE.txt
 */

package hera.example.wallet.transaction;

import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.TxHash;
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.example.AbstractExample;
import hera.key.AergoKey;
import hera.keystore.InMemoryKeyStore;
import hera.keystore.KeyStore;
import hera.wallet.WalletApi;
import hera.wallet.WalletFactory;

public class NamingAccount extends AbstractExample {

  @Override
  public void run() throws Exception {
    // make keystore and save key
    KeyStore keyStore = new InMemoryKeyStore();
    AergoKey key = supplyKey();
    Authentication authentication = Authentication.of(key.getAddress(), "password");
    keyStore.save(authentication, key);

    // make wallet api
    WalletApi walletApi = new WalletFactory().create(keyStore);

    // make aergo client
    AergoClient aergoClient = new AergoClientBuilder()
        .withEndpoint(hostname)
        .withNonBlockingConnect()
        .build();

    // bind aergo client
    walletApi.bind(aergoClient);

    // unlock account
    walletApi.unlock(authentication);

    // create name
    String name = "namenamename";
    TxHash namingTxHash = walletApi.transactionApi().createName(name);
    System.out.println("Naming transaciton hash: " + namingTxHash);

    // sleep
    Thread.sleep(2200L);

    // check name owner
    AccountAddress owner = walletApi.queryApi().getNameOwner(name);
    System.out.println("Name owner: " + owner);

    // lock an wallet
    walletApi.lock(authentication);

    // close the client
    aergoClient.close();
  }

  public static void main(String[] args) throws Exception {
    new NamingAccount().run();
  }


}
