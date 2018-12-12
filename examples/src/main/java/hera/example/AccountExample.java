/*
 * @copyright defined in LICENSE.txt
 */

package hera.example;

import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountFactory;
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.util.Configuration;
import hera.util.conf.InMemoryConfiguration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class AccountExample extends AbstractExample {

  protected void createWithKey() {
    // set configuration
    final Configuration configuration = new InMemoryConfiguration();
    configuration.define("endpoint", "localhost:7845");

    // make aergo client object
    final AergoClient aergoClient = new AergoClientBuilder()
        .withEndpoint("localhost:7845")
        .withNonBlockingConnect()
        .build();

    // create aergokey
    final AergoKey key = new AergoKeyGenerator().create();
    final Account account = new AccountFactory().create(key);
    System.out.println("Created account: " + account);

    // close the client
    aergoClient.close();
  }

  protected void createWithPassword() {
    // make aergo client object
    final AergoClient aergoClient = new AergoClientBuilder()
        .withEndpoint("localhost:7845")
        .withNonBlockingConnect()
        .withTimeout(10L, TimeUnit.SECONDS)
        .build();

    // create an account which store an encrypted key in a server
    final String password = "password";
    final Account account = aergoClient.getKeyStoreOperation().create(password);
    System.out.println("Created account: " + account);

    // query account list
    final List<AccountAddress> accountList = aergoClient.getKeyStoreOperation().list();
    System.out.println("Account list: " + accountList);

    Optional<AccountAddress> filtered =
        accountList.stream().filter(a -> a.equals(account.getAddress())).findFirst();
    System.out.println("Filter result: " + filtered.isPresent());

    // close the client
    aergoClient.close();
  }

  @Override
  public void run() {
    createWithKey();
    createWithPassword();
  }

  public static void main(String[] args) {
    new AccountExample().run();
  }

}
