/*
 * @copyright defined in LICENSE.txt
 */

package hera.example.wallet.transaction;

import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.Authentication;
import hera.api.model.Fee;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.example.AbstractExample;
import hera.key.AergoKey;
import hera.keystore.InMemoryKeyStore;
import hera.keystore.KeyStore;
import hera.wallet.WalletApi;
import hera.wallet.WalletFactory;

public class SendingAergo extends AbstractExample {

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
    boolean unlockResult = walletApi.unlock(authentication);
    System.out.println("Unlock account result " + unlockResult);

    // send aergo
    AccountAddress recipient =
        AccountAddress.of("AmLbHdVs4dNpRzyLirs8cKdV26rPJJxpVXG1w2LLZ9pKfqAHHdyg");
    TxHash txHash = walletApi.transactionApi().send(recipient, Aer.of("100", Unit.GAER), Fee.EMPTY);
    System.out.println("Sending TxHash: " + txHash);

    // get sending transaction
    Transaction transaction = walletApi.queryApi().getTransaction(txHash);
    System.out.println("Transaction: " + transaction);
    
    // lock an wallet
    walletApi.lock(authentication);

    // close the client
    aergoClient.close();
  }

  public static void main(String[] args) throws Exception {
    new SendingAergo().run();
  }

}
