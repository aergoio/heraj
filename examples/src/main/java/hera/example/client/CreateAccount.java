/*
 * @copyright defined in LICENSE.txt
 */

package hera.example.client;

import hera.api.model.AccountAddress;
import hera.api.model.EncryptedPrivateKey;
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.example.AbstractExample;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import java.util.List;

public class CreateAccount extends AbstractExample {

  protected void createNewAergoKey() {
    // create aergokey
    final AergoKey key = new AergoKeyGenerator().create();
    System.out.println("Created key: " + key);
  }

  protected void createWithExported() {
    // create aergokey
    final String encryptedKey =
        "47pArdc5PNS9HYY9jMMC7zAuHzytzsAuCYGm5jAUFuD3amQ4mQkvyUaPnmRVSPc2iWzVJpC9Z";
    final String encryptPassword = "password";
    final AergoKey key = AergoKey.of(encryptedKey, encryptPassword);
    System.out.println("Created key: " + key);
  }

  protected void createWithServerKeyStore() {
    // make aergo client object
    final AergoClient aergoClient = new AergoClientBuilder()
        .withEndpoint(hostname)
        .withNonBlockingConnect()
        .build();

    // create an account which store an encrypted key in a server
    final String password = "some_password";
    final AccountAddress created = aergoClient.getKeyStoreOperation().create(password);
    System.out.println("Created account: " + created);

    // query account list
    final List<AccountAddress> accountList = aergoClient.getKeyStoreOperation().list();
    System.out.println("Account list: " + accountList);

    // find an account from remote keystore
    boolean findIt = false;
    for (AccountAddress keyStoreAccount : aergoClient.getKeyStoreOperation().list()) {
      if (keyStoreAccount.equals(created)) {
        findIt = true;
        break;
      }
    }
    System.out.println("Find result: " + findIt);

    // close the client
    aergoClient.close();
  }

  protected void importToServerKeyStore() {
    // make aergo client object
    final AergoClient aergoClient = new AergoClientBuilder()
        .withEndpoint(hostname)
        .withNonBlockingConnect()
        .build();

    // create aergokey
    final AergoKey key = new AergoKeyGenerator().create();
    final String password = "some_password";
    final EncryptedPrivateKey encryptedKey = key.export(password);

    final AccountAddress imported =
        aergoClient.getKeyStoreOperation().importKey(encryptedKey, password, password);
    System.out.println("Imported account: " + imported);

    // query account list
    final List<AccountAddress> accountList = aergoClient.getKeyStoreOperation().list();
    System.out.println("Account list: " + accountList);

    // find an account from remote keystore
    boolean findIt = false;
    for (AccountAddress keyStoreAccount : aergoClient.getKeyStoreOperation().list()) {
      if (keyStoreAccount.equals(imported)) {
        findIt = true;
        break;
      }
    }
    System.out.println("Find result: " + findIt);

    // close the client
    aergoClient.close();
  }

  @Override
  public void run() {
    createNewAergoKey();
    createWithExported();
    createWithServerKeyStore();
    importToServerKeyStore();
  }

  public static void main(String[] args) {
    new CreateAccount().run();
  }

}
