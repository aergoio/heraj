/*
 * @copyright defined in LICENSE.txt
 */

package hera.example.client;

import static java.util.UUID.randomUUID;

import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountFactory;
import hera.api.model.EncryptedPrivateKey;
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.example.AbstractExample;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import java.util.List;

public class CreateAccount extends AbstractExample {

  protected void createWithLocalKey() {
    // create aergokey
    final AergoKey key = new AergoKeyGenerator().create();

    // create account
    final Account account = new AccountFactory().create(key);
    System.out.println("Created account: " + account);
  }

  protected void createWithExported() {
    // create aergokey
    final String encryptedKey =
        "47pArdc5PNS9HYY9jMMC7zAuHzytzsAuCYGm5jAUFuD3amQ4mQkvyUaPnmRVSPc2iWzVJpC9Z";
    final String encryptPassword = "password";
    final AergoKey key = AergoKey.of(encryptedKey, encryptPassword);

    // create account
    final Account account = new AccountFactory().create(key);
    System.out.println("Created account: " + account);
  }

  protected void createWithServerKeyStore() {
    // make aergo client object
    final AergoClient aergoClient = new AergoClientBuilder()
        .withEndpoint(hostname)
        .withNonBlockingConnect()
        .build();

    // create an account which store an encrypted key in a server
    final String password = randomUUID().toString();
    final Account account = aergoClient.getKeyStoreOperation().create(password);
    System.out.println("Created account: " + account);

    // query account list
    final List<AccountAddress> accountList = aergoClient.getKeyStoreOperation().list();
    System.out.println("Account list: " + accountList);

    // find an account from remote keystore
    boolean findIt = false;
    for (AccountAddress keyStoreAccount : aergoClient.getKeyStoreOperation().list()) {
      if (keyStoreAccount.equals(account.getAddress())) {
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
    final String password = randomUUID().toString();
    final EncryptedPrivateKey encryptedKey = key.export(password);

    final Account account =
        aergoClient.getKeyStoreOperation().importKey(encryptedKey, password, password);
    System.out.println("Imported account: " + account);

    // query account list
    final List<AccountAddress> accountList = aergoClient.getKeyStoreOperation().list();
    System.out.println("Account list: " + accountList);

    // find an account from remote keystore
    boolean findIt = false;
    for (AccountAddress keyStoreAccount : aergoClient.getKeyStoreOperation().list()) {
      if (keyStoreAccount.equals(account.getAddress())) {
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
    createWithLocalKey();
    createWithExported();
    createWithServerKeyStore();
    importToServerKeyStore();
  }

  public static void main(String[] args) {
    new CreateAccount().run();
  }

}
