/*
 * @copyright defined in LICENSE.txt
 */

package hera.example;

import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.ClientManagedAccount;
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.strategy.NettyConnectStrategy;
import java.util.List;
import java.util.Optional;

public class AccountExample extends AbstractExample {

  protected void createWithKey() {
    // make aergo client object
    final AergoClient aergoClient =
        new AergoClientBuilder().addStrategy(new NettyConnectStrategy(hostname)).build();

    // create aergokey
    final AergoKey key = new AergoKeyGenerator().create();
    final Account account = ClientManagedAccount.of(key);
    System.out.println("Created account: " + account);

    // close the client
    aergoClient.close();
  }

  protected void createWithPassword() {
    // make aergo client object
    final AergoClient aergoClient =
        new AergoClientBuilder().addStrategy(new NettyConnectStrategy(hostname)).build();

    // create an account which store an encrypted key in a server
    final String password = "password";
    final Account account = aergoClient.getAccountOperation().create(password);
    System.out.println("Created account: " + account);

    // query account list
    final List<AccountAddress> accountList = aergoClient.getAccountOperation().list();
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
