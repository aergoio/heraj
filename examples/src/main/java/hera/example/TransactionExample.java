/*
 * @copyright defined in LICENSE.txt
 */

package hera.example;

import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.Authentication;
import hera.api.model.ClientManagedAccount;
import hera.api.model.ServerManagedAccount;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.key.AergoKey;

public class TransactionExample extends AbstractExample {

  protected void buildAndCommitWithKey() {
    // make aergo client object
    final AergoClient aergoClient = new AergoClientBuilder()
        .withEndpoint("localhost:7845")
        .withNonBlockingConnect()
        .build();

    // create an account
    final AergoKey key = AergoKey.of(
        "47pArdc5PNS9HYY9jMMC7zAuHzytzsAuCYGm5jAUFuD3amQ4mQkvyUaPnmRVSPc2iWzVJpC9Z", "password");
    final Account account = ClientManagedAccount.of(key);
    final AccountState state = aergoClient.getAccountOperation().getState(account);
    account.bindState(state);

    // make a transaction
    final Transaction transaction = new Transaction();
    transaction.setNonce(account.nextNonce());
    transaction.setAmount(10L);
    transaction.setSender(account);
    transaction.setRecipient(
        AccountAddress.of(() -> "AmLbHdVs4dNpRzyLirs8cKdV26rPJJxpVXG1w2LLZ9pKfqAHHdyg"));
    final Signature signature = aergoClient.getAccountOperation().sign(account, transaction);
    transaction.setSignature(signature);

    // commit request
    final TxHash txHash = aergoClient.getTransactionOperation().commit(transaction);
    System.out.println("TxHash: " + txHash);

    // query tx with hash
    final Transaction queried = aergoClient.getTransactionOperation().getTransaction(txHash);
    System.out.println("Queired transaction: " + queried);

    // close the client
    aergoClient.close();
  }

  protected void buildAndCommitWithPassword() {
    // make aergo client object
    final AergoClient aergoClient = new AergoClientBuilder()
        .withEndpoint("localhost:7845")
        .withNonBlockingConnect()
        .build();

    // create an account
    final Account account = ServerManagedAccount
        .of(AccountAddress.of(() -> "AmM25FKSK1gCqSdUPjnvESsauESNgfZUauHWp7R8Un3zHffEQgTm"));
    final String password = "password";
    final AccountState state = aergoClient.getAccountOperation().getState(account);
    account.bindState(state);

    // unlock an account
    aergoClient.getKeyStoreOperation().unlock(Authentication.of(account.getAddress(), password));

    // make a transaction
    final Transaction transaction = new Transaction();
    transaction.setNonce(account.nextNonce());
    transaction.setAmount(10L);
    transaction.setSender(account);
    transaction.setRecipient(
        AccountAddress.of(() -> "AmLbHdVs4dNpRzyLirs8cKdV26rPJJxpVXG1w2LLZ9pKfqAHHdyg"));
    final Signature signature = aergoClient.getAccountOperation().sign(account, transaction);
    transaction.setSignature(signature);

    // commit request
    final TxHash txHash = aergoClient.getTransactionOperation().commit(transaction);
    System.out.println("TxHash: " + txHash);

    // query tx with hash
    final Transaction queried = aergoClient.getTransactionOperation().getTransaction(txHash);
    System.out.println("Queired transaction: " + queried);

    // lock an account
    aergoClient.getKeyStoreOperation().lock(Authentication.of(account.getAddress(), password));

    // close the client
    aergoClient.close();
  }

  protected void sendTransaction() {
    // make aergo client object
    final AergoClient aergoClient = new AergoClientBuilder()
        .withEndpoint("localhost:7845")
        .withNonBlockingConnect()
        .build();

    // create a sender
    final Account account = ServerManagedAccount
        .of(AccountAddress.of(() -> "AmM25FKSK1gCqSdUPjnvESsauESNgfZUauHWp7R8Un3zHffEQgTm"));
    final String password = "password";

    // unlock before send tx
    aergoClient.getKeyStoreOperation().unlock(Authentication.of(account.getAddress(), password));

    // send tx
    final TxHash txHash = aergoClient.getTransactionOperation().send(account.getAddress(),
        AccountAddress.of(() -> "AmLbHdVs4dNpRzyLirs8cKdV26rPJJxpVXG1w2LLZ9pKfqAHHdyg"), 10L);
    System.out.println("TxHash: " + txHash);

    // query tx with hash
    final Transaction queried = aergoClient.getTransactionOperation().getTransaction(txHash);
    System.out.println("Queired transaction: " + queried);

    // lock after it
    aergoClient.getKeyStoreOperation().lock(Authentication.of(account.getAddress(), password));

    // close the client
    aergoClient.close();
  }

  @Override
  public void run() {
    buildAndCommitWithKey();
    sleep(1500L);
    buildAndCommitWithPassword();
    sleep(1500L);
    sendTransaction();
  }

  public static void main(String[] args) {
    new TransactionExample().run();
  }

}
