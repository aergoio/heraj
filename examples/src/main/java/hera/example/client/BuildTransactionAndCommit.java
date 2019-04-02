/*
 * @copyright defined in LICENSE.txt
 */

package hera.example.client;

import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountFactory;
import hera.api.model.Aer.Unit;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.example.AbstractExample;
import hera.key.AergoKeyGenerator;

public class BuildTransactionAndCommit extends AbstractExample {

  protected void buildAndCommit() {
    // make aergo client object
    final AergoClient aergoClient = new AergoClientBuilder()
        .withEndpoint(hostname)
        .withNonBlockingConnect()
        .build();
    
    // cache chain id hash
    aergoClient.cacheChainIdHash(aergoClient.getBlockchainOperation().getChainIdHash());

    // create an account
    final Account account = new AccountFactory().create(new AergoKeyGenerator().create());

    // funding account
    fund(account.getAddress());

    // make a transaction
    final RawTransaction rawTransaction = Transaction.newBuilder(aergoClient.getCachedChainIdHash())
        .from(account)
        .to(AccountAddress.of("AmLbHdVs4dNpRzyLirs8cKdV26rPJJxpVXG1w2LLZ9pKfqAHHdyg"))
        .amount("10", Unit.GAER)
        .nonce(account.incrementAndGetNonce())
        .build();

    // sign a transaction
    final Transaction signedTransaction =
        aergoClient.getAccountOperation().sign(account, rawTransaction);

    // commit request
    final TxHash txHash = aergoClient.getTransactionOperation().commit(signedTransaction);
    System.out.println("TxHash: " + txHash);

    // query tx with hash
    final Transaction queried = aergoClient.getTransactionOperation().getTransaction(txHash);
    System.out.println("Queired transaction: " + queried);

    // close the client
    aergoClient.close();
  }

  @Override
  public void run() {
    buildAndCommit();
  }

  public static void main(String[] args) {
    new BuildTransactionAndCommit().run();
  }

}
