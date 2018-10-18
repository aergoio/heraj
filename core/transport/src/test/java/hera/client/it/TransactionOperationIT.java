/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.Authentication;
import hera.api.model.Block;
import hera.api.model.ClientManagedAccount;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.strategy.NettyConnectStrategy;
import java.util.Optional;
import org.junit.Test;

public class TransactionOperationIT extends AbstractIT {

  protected final int amount = 30;

  @Test
  public void testCommitOnLockedAccount() throws Exception {
    final AergoClient aergoClient =
        new AergoClientBuilder().addStrategy(new NettyConnectStrategy(hostname)).build();

    final String password = randomUUID().toString();

    final Account remoteAccount = aergoClient.getAccountOperation().create(password);
    final AccountAddress recipient = new AergoKeyGenerator().create().getAddress();

    final Transaction transaction = new Transaction();
    transaction.setNonce(remoteAccount.getNonceAndImcrement());
    transaction.setAmount(amount);
    transaction.setSender(remoteAccount.getAddress());
    transaction.setRecipient(recipient);

    try {
      final Signature signature =
          aergoClient.getAccountOperation().sign(remoteAccount, transaction);
      fail();
      transaction.setSignature(signature);
    } catch (Exception e) {
      // good we expected this
      logger.info(e.getLocalizedMessage());
    }

    try {
      aergoClient.getTransactionOperation().commit(transaction);
      fail();
    } catch (Exception e) {
      // good we expected this
      logger.info(e.getLocalizedMessage());
    }

    aergoClient.close();
  }

  @Test
  public void testCommitBySigningRemotely() throws Exception {
    final AergoClient aergoClient =
        new AergoClientBuilder().addStrategy(new NettyConnectStrategy(hostname)).build();

    final String password = randomUUID().toString();

    final Account remoteAccount = aergoClient.getAccountOperation().create(password);
    final AccountAddress recipient = new AergoKeyGenerator().create().getAddress();

    final boolean unlockResult = aergoClient.getAccountOperation()
        .unlock(Authentication.of(remoteAccount.getAddress(), password));
    assertTrue(unlockResult);

    final Transaction transaction = new Transaction();
    transaction.setNonce(remoteAccount.getNonceAndImcrement());
    transaction.setAmount(amount);
    transaction.setSender(remoteAccount.getAddress());
    transaction.setRecipient(recipient);
    final Signature signature = aergoClient.getAccountOperation().sign(remoteAccount, transaction);
    transaction.setSignature(signature);
    logger.info("Ready Transaction: {}", transaction);
    final TxHash txHash = aergoClient.getTransactionOperation().commit(transaction);
    logger.info("TxHash: {}", txHash);

    final Transaction queried = aergoClient.getTransactionOperation().getTransaction(txHash);
    logger.info("Queired transaction: {}", queried);
    assertEquals(transaction, queried);
    assertFalse(queried.isConfirmed());

    final boolean lockResult = aergoClient.getAccountOperation()
        .lock(Authentication.of(remoteAccount.getAddress(), password));
    assertTrue(lockResult);

    aergoClient.close();
  }

  @Test
  public void testCommitBySigningLocally() throws Exception {
    final AergoClient aergoClient =
        new AergoClientBuilder().addStrategy(new NettyConnectStrategy(hostname)).build();

    final AergoKey key = new AergoKeyGenerator().create();
    final Account sender = ClientManagedAccount.of(key);
    final AccountAddress recipient = new AergoKeyGenerator().create().getAddress();

    final Transaction transaction = new Transaction();
    transaction.setNonce(1);
    transaction.setAmount(amount);
    transaction.setSender(sender);
    transaction.setRecipient(recipient);
    final Signature signature = aergoClient.getAccountOperation().sign(sender, transaction);
    transaction.setSignature(signature);
    logger.info("Ready Transaction: {}", transaction);
    final TxHash txHash = aergoClient.getTransactionOperation().commit(transaction);
    logger.info("TxHash: {}", txHash);

    final Transaction queried = aergoClient.getTransactionOperation().getTransaction(txHash);
    logger.info("Queired transaction: {}", queried);
    assertEquals(transaction, queried);
    assertFalse(queried.isConfirmed());

    aergoClient.close();
  }

  @Test
  public void testCommitAndQueryAfterConfirmed() throws Exception {
    final AergoClient aergoClient =
        new AergoClientBuilder().addStrategy(new NettyConnectStrategy(hostname)).build();

    final AergoKey key = new AergoKeyGenerator().create();
    final Account localAccount = ClientManagedAccount.of(key);
    final AccountAddress recipient = new AergoKeyGenerator().create().getAddress();

    final Transaction transaction = new Transaction();
    transaction.setNonce(1);
    transaction.setAmount(amount);
    transaction.setSender(localAccount);
    transaction.setRecipient(recipient);
    final Signature signature = aergoClient.getAccountOperation().sign(localAccount, transaction);
    transaction.setSignature(signature);
    logger.info("Ready Transaction: {}", transaction);
    final TxHash txHash = aergoClient.getTransactionOperation().commit(transaction);
    logger.info("TxHash: {}", txHash);

    waitForNextBlockToGenerate();

    final Transaction queried = aergoClient.getTransactionOperation().getTransaction(txHash);
    logger.info("Queired transaction: {}", queried);
    assertNotEquals(transaction, queried);
    assertTrue(queried.isConfirmed());

    final Block confirmBlock = aergoClient.getBlockOperation().getBlock(queried.getBlockHash());
    logger.info("Comfirm block: {}", confirmBlock);
    assertEquals(queried.getBlockHash(), confirmBlock.getHash());

    final Optional<Transaction> txInBlock =
        confirmBlock.getTransactions().stream().filter(t -> t.equals(queried)).findFirst();
    assertTrue(txInBlock.isPresent());

    final AccountState senderState = aergoClient.getAccountOperation().getState(localAccount);
    final AccountState recipientState = aergoClient.getAccountOperation().getState(recipient);
    assertEquals(1, senderState.getNonce());
    assertEquals(amount, recipientState.getBalance());

    aergoClient.close();
  }

  @Test
  public void testSendTransaction() throws Exception {
    final AergoClient aergoClient =
        new AergoClientBuilder().addStrategy(new NettyConnectStrategy(hostname)).build();

    final String password = randomUUID().toString();

    final Account sender = aergoClient.getAccountOperation().create(password);
    final AccountAddress recipient = new AergoKeyGenerator().create().getAddress();

    final boolean unlockResult =
        aergoClient.getAccountOperation().unlock(Authentication.of(sender.getAddress(), password));
    assertTrue(unlockResult);

    final TxHash txHash =
        aergoClient.getTransactionOperation().send(sender.getAddress(), recipient, amount);
    logger.info("TxHash: {}", txHash);

    waitForNextBlockToGenerate();

    final Transaction queried = aergoClient.getTransactionOperation().getTransaction(txHash);
    logger.info("Queired transaction: {}", queried);
    assertTrue(queried.isConfirmed());

    final AccountState senderState =
        aergoClient.getAccountOperation().getState(sender.getAddress());
    final AccountState recipientState = aergoClient.getAccountOperation().getState(recipient);
    assertEquals(1, senderState.getNonce());
    assertEquals(amount, recipientState.getBalance());

    final boolean lockResult =
        aergoClient.getAccountOperation().lock(Authentication.of(sender.getAddress(), password));
    assertTrue(lockResult);

    aergoClient.close();
  }

}
