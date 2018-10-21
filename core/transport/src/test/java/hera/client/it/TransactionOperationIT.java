/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
import hera.key.AergoKeyGenerator;
import hera.strategy.NettyConnectStrategy;
import org.junit.Before;
import org.junit.Test;

public class TransactionOperationIT extends AbstractIT {

  @Before
  public void setUp() throws Exception {
    super.setUp();
  }

  @Test
  public void testCommitBySigningLocally() throws Exception {
    // make aergo client object
    final AergoClient aergoClient =
        new AergoClientBuilder().addStrategy(new NettyConnectStrategy(hostname)).build();

    // create an account
    final AergoKey key = new AergoKeyGenerator().create();
    final ClientManagedAccount account = ClientManagedAccount.of(key);

    // fulfill the balance
    aergoClient.getAccountOperation().unlock(Authentication.of(rich.getAddress(), richPassword));
    aergoClient.getTransactionOperation().send(rich, account, 10L);
    aergoClient.getAccountOperation().lock(Authentication.of(rich.getAddress(), richPassword));

    waitForNextBlockToGenerate();

    // make a transaction
    final Transaction transaction = new Transaction();
    transaction.setNonce(account.nextNonce());
    transaction.setAmount(1L);
    transaction.setSender(account);
    transaction.setRecipient(
        AccountAddress.of(() -> "AmLbHdVs4dNpRzyLirs8cKdV26rPJJxpVXG1w2LLZ9pKfqAHHdyg"));
    logger.info("Raw transaction: {}", transaction);

    // sign it
    final Signature signature = aergoClient.getAccountOperation().sign(account, transaction);
    logger.info("Signature: {}", signature);

    // fill signature with previous one
    transaction.setSignature(signature);
    logger.info("Signed transaction: {}", transaction);

    // commit request
    final TxHash txHash = aergoClient.getTransactionOperation().commit(transaction);
    logger.info("TxHash: {}", txHash);

    // query tx with hash
    final Transaction queried = aergoClient.getTransactionOperation().getTransaction(txHash);
    logger.info("Queired transaction: {}", queried);

    assertEquals(transaction, queried);
    assertFalse(queried.isConfirmed());

    // close the client
    aergoClient.close();
  }

  @Test
  public void testCommitAndWaitToConfirmBySigningLocally() throws Exception {
    // make aergo client object
    final AergoClient aergoClient =
        new AergoClientBuilder().addStrategy(new NettyConnectStrategy(hostname)).build();

    // create an account
    final AergoKey key = new AergoKeyGenerator().create();
    final ClientManagedAccount account = ClientManagedAccount.of(key);

    // fulfill the balance
    aergoClient.getAccountOperation().unlock(Authentication.of(rich.getAddress(), richPassword));
    aergoClient.getTransactionOperation().send(rich, account, 10L);
    aergoClient.getAccountOperation().lock(Authentication.of(rich.getAddress(), richPassword));

    waitForNextBlockToGenerate();

    // make a transaction
    final Transaction transaction = new Transaction();
    transaction.setNonce(account.nextNonce());
    transaction.setAmount(1L);
    transaction.setSender(account);
    transaction.setRecipient(
        AccountAddress.of(() -> "AmLbHdVs4dNpRzyLirs8cKdV26rPJJxpVXG1w2LLZ9pKfqAHHdyg"));
    logger.info("Raw transaction: {}", transaction);

    // sign it
    final Signature signature = aergoClient.getAccountOperation().sign(account, transaction);
    logger.info("Signature: {}", signature);

    // fill signature with previous one
    transaction.setSignature(signature);
    logger.info("Signed transaction: {}", transaction);

    // commit request
    final TxHash txHash = aergoClient.getTransactionOperation().commit(transaction);
    logger.info("TxHash: {}", txHash);

    waitForNextBlockToGenerate();

    // query tx with hash
    final Transaction queried = aergoClient.getTransactionOperation().getTransaction(txHash);
    logger.info("Queired transaction: {}", queried);

    assertTrue(queried.isConfirmed());

    // close the client
    aergoClient.close();
  }

  @Test
  public void testCommitBySigningRemotely() throws Exception {
    // make aergo client object
    final AergoClient aergoClient =
        new AergoClientBuilder().addStrategy(new NettyConnectStrategy(hostname)).build();

    // create an account
    final String password = "password";
    final ServerManagedAccount account = aergoClient.getAccountOperation().create(password);

    // fulfill the balance
    aergoClient.getAccountOperation().unlock(Authentication.of(rich.getAddress(), richPassword));
    aergoClient.getTransactionOperation().send(rich, account, 10L);
    aergoClient.getAccountOperation().lock(Authentication.of(rich.getAddress(), richPassword));

    waitForNextBlockToGenerate();

    // make a transaction
    final Transaction transaction = new Transaction();
    transaction.setNonce(account.nextNonce());
    transaction.setAmount(1L);
    transaction.setSender(account);
    transaction.setRecipient(
        AccountAddress.of(() -> "AmLbHdVs4dNpRzyLirs8cKdV26rPJJxpVXG1w2LLZ9pKfqAHHdyg"));
    logger.info("Raw transaction: {}", transaction);

    // unlock account before sign it
    aergoClient.getAccountOperation().unlock(Authentication.of(account.getAddress(), password));

    // sign it
    final Signature signature = aergoClient.getAccountOperation().sign(account, transaction);
    logger.info("Signature: {}", signature);

    // lock after sign it
    aergoClient.getAccountOperation().lock(Authentication.of(account.getAddress(), password));

    // fill signature with previous one
    transaction.setSignature(signature);
    logger.info("Signed transaction: {}", transaction);

    // commit request
    final TxHash txHash = aergoClient.getTransactionOperation().commit(transaction);
    logger.info("TxHash: {}", txHash);

    // query tx with hash
    final Transaction queried = aergoClient.getTransactionOperation().getTransaction(txHash);
    logger.info("Queired transaction: {}", queried);

    assertEquals(transaction, queried);
    assertFalse(queried.isConfirmed());

    // close the client
    aergoClient.close();
  }

  @Test
  public void testCommitAndWaitToConfirmBySigningRemotely() throws Exception {
    // make aergo client object
    final AergoClient aergoClient =
        new AergoClientBuilder().addStrategy(new NettyConnectStrategy(hostname)).build();

    // create an account
    final String password = "password";
    final ServerManagedAccount account = aergoClient.getAccountOperation().create(password);

    // fulfill the balance
    aergoClient.getAccountOperation().unlock(Authentication.of(rich.getAddress(), richPassword));
    aergoClient.getTransactionOperation().send(rich, account, 10L);
    aergoClient.getAccountOperation().lock(Authentication.of(rich.getAddress(), richPassword));

    waitForNextBlockToGenerate();

    // make a transaction
    final Transaction transaction = new Transaction();
    transaction.setNonce(account.nextNonce());
    transaction.setAmount(1L);
    transaction.setSender(account);
    transaction.setRecipient(
        AccountAddress.of(() -> "AmLbHdVs4dNpRzyLirs8cKdV26rPJJxpVXG1w2LLZ9pKfqAHHdyg"));
    logger.info("Raw transaction: {}", transaction);

    // unlock account before sign it
    aergoClient.getAccountOperation().unlock(Authentication.of(account.getAddress(), password));

    // sign it
    final Signature signature = aergoClient.getAccountOperation().sign(account, transaction);
    logger.info("Signature: {}", signature);

    // lock after sign it
    aergoClient.getAccountOperation().lock(Authentication.of(account.getAddress(), password));

    // fill signature with previous one
    transaction.setSignature(signature);
    logger.info("Signed transaction: {}", transaction);

    // commit request
    final TxHash txHash = aergoClient.getTransactionOperation().commit(transaction);
    logger.info("TxHash: {}", txHash);

    waitForNextBlockToGenerate();

    // query tx with hash
    final Transaction queried = aergoClient.getTransactionOperation().getTransaction(txHash);
    logger.info("Queired transaction: {}", queried);

    assertTrue(queried.isConfirmed());

    // close the client
    aergoClient.close();
  }

  @Test
  public void testSendTx() throws Exception {
    // make aergo client object
    final AergoClient aergoClient =
        new AergoClientBuilder().addStrategy(new NettyConnectStrategy(hostname)).build();

    // create a sender
    final String password = "password";
    final ServerManagedAccount sender = aergoClient.getAccountOperation().create(password);

    // fulfill the balance
    aergoClient.getAccountOperation().unlock(Authentication.of(rich.getAddress(), richPassword));
    aergoClient.getTransactionOperation().send(rich, sender, 10L);
    aergoClient.getAccountOperation().lock(Authentication.of(rich.getAddress(), richPassword));

    waitForNextBlockToGenerate();

    // create a recipient
    final AergoKey key = new AergoKeyGenerator().create();
    final ClientManagedAccount recipient = ClientManagedAccount.of(key);

    // get state of a recipient
    final AccountState preState = aergoClient.getAccountOperation().getState(recipient);
    logger.info("Before donation: {}", preState);

    // must have no balance
    assertEquals(0, preState.getBalance());

    // unlock before send tx
    aergoClient.getAccountOperation().unlock(Authentication.of(sender.getAddress(), password));

    // send tx
    aergoClient.getTransactionOperation().send(sender, recipient, 3L);

    // lock after it
    aergoClient.getAccountOperation().lock(Authentication.of(sender.getAddress(), password));

    waitForNextBlockToGenerate();

    // get state of an account after donation
    final AccountState postState = aergoClient.getAccountOperation().getState(recipient);
    logger.info("After donation: {}", postState);

    // now the poor has 10 aergo
    assertEquals(3L, postState.getBalance());

    // close the client
    aergoClient.close();
  }

}
