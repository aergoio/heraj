/*
 * @copyright defined in LICENSE.txt
 */

package hera.example.highlevel;

import static java.util.UUID.randomUUID;

import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.Authentication;
import hera.api.model.Fee;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.example.AbstractExample;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.wallet.Wallet;
import hera.wallet.WalletBuilder;
import hera.wallet.WalletType;
import java.security.KeyStore;

public class SendAergo extends AbstractExample {

  protected String keyStorePath =
      System.getProperty("java.io.tmpdir") + "/" + randomUUID().toString();
  protected String keyStorePasword = "password";

  protected void sendAergo() {
    // make aergo client object
    final Wallet wallet = new WalletBuilder()
        .withEndpoint(hostname)
        .withNonBlockingConnect()
        .build(WalletType.Naive);

    // create an account
    final AergoKey key = new AergoKeyGenerator().create();
    final String password = randomUUID().toString();

    // save and unlock it
    wallet.saveKey(key, password);
    wallet.unlock(Authentication.of(key.getAddress(), password));

    // send aergo
    final AccountAddress recipient =
        AccountAddress.of("AmLbHdVs4dNpRzyLirs8cKdV26rPJJxpVXG1w2LLZ9pKfqAHHdyg");
    final TxHash txHash = wallet.send(recipient, Aer.of("10", Unit.GAER), Fee.getDefaultFee());
    System.out.println("TxHash: " + txHash);

    // query tx with hash
    final Transaction queried = wallet.getTransaction(txHash);
    System.out.println("Queired transaction: " + queried);

    sleep(1500L);

    // query tx after confirmed
    final Transaction confirmed = wallet.getTransaction(txHash);
    System.out.println("Queired transaction: " + confirmed.isConfirmed());

    // close the wallet
    wallet.close();
  }

  protected void sendAergoWithSecure() throws Exception {
    // make aergo client object
    final Wallet wallet = new WalletBuilder()
        .withEndpoint(hostname)
        .withNonBlockingConnect()
        .build(WalletType.Secure);

    // here, make a new keystore
    // if exists, load with input stream and password
    final KeyStore keyStore = KeyStore.getInstance("JKS");
    keyStore.load(null, null);

    // bind keystore
    wallet.bindKeyStore(keyStore);

    // create an account
    final AergoKey key = new AergoKeyGenerator().create();
    final String password = randomUUID().toString();

    // save and unlock it
    wallet.saveKey(key, password);
    wallet.unlock(Authentication.of(key.getAddress(), password));

    // send aergo
    final AccountAddress recipient =
        AccountAddress.of("AmLbHdVs4dNpRzyLirs8cKdV26rPJJxpVXG1w2LLZ9pKfqAHHdyg");
    final TxHash txHash = wallet.send(recipient, Aer.of("10", Unit.GAER), Fee.getDefaultFee());
    System.out.println("TxHash: " + txHash);

    // query tx with hash
    final Transaction queried = wallet.getTransaction(txHash);
    System.out.println("Queired transaction: " + queried);

    sleep(1500L);

    // query tx after confirmed
    final Transaction confirmed = wallet.getTransaction(txHash);
    System.out.println("Queired transaction: " + confirmed.isConfirmed());

    // save keystore
    wallet.storeKeyStore(keyStorePath, keyStorePasword);

    // close the wallet
    wallet.close();
  }

  @Override
  public void run() throws Exception {
    sendAergo();
    sendAergoWithSecure();
  }

  public static void main(String[] args) throws Exception {
    new SendAergo().run();
  }

}
