/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import hera.api.model.Account;
import hera.api.model.AccountState;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.exception.CommitException;
import org.junit.Test;

public class TransactionOperationIT extends AbstractIT {

  @Test
  public void testCommitBySigningLocally() throws Exception {
    final Account account = createClientAccount();

    final RawTransaction transaction = buildTransaction(account);
    final Transaction signed = signTransaction(account, transaction);

    final TxHash txHash = aergoClient.getTransactionOperation().commit(signed);
    logger.info("TxHash: {}", txHash);

    final Transaction queried = aergoClient.getTransactionOperation().getTransaction(txHash);
    logger.info("Queired transaction: {}", queried);

    assertTrue(!queried.isConfirmed());
  }

  @Test
  public void testCommitAndWaitToConfirmBySigningLocally() throws Exception {
    final Account account = createClientAccount();

    final RawTransaction transaction = buildTransaction(account);
    final Transaction signed = signTransaction(account, transaction);

    final TxHash txHash = aergoClient.getTransactionOperation().commit(signed);
    logger.info("TxHash: {}", txHash);

    waitForNextBlockToGenerate();

    final Transaction queried = aergoClient.getTransactionOperation().getTransaction(txHash);
    logger.info("Queired transaction: {}", queried);

    assertTrue(queried.isConfirmed());
  }

  @Test
  public void testCommitBySigningRemotely() throws Exception {
    final String password = randomUUID().toString();
    final Account account = createServerAccount(password);

    final RawTransaction transaction = buildTransaction(account);

    assertTrue(unlockAccount(account, password));
    final Transaction signed = signTransaction(account, transaction);
    assertTrue(lockAccount(account, password));

    final TxHash txHash = aergoClient.getTransactionOperation().commit(signed);
    logger.info("TxHash: {}", txHash);

    final Transaction queried = aergoClient.getTransactionOperation().getTransaction(txHash);
    logger.info("Queired transaction: {}", queried);

    assertTrue(!queried.isConfirmed());
  }

  @Test
  public void testCommitAndWaitToConfirmBySigningRemotely() throws Exception {
    final String password = randomUUID().toString();
    final Account account = createServerAccount(password);

    final RawTransaction transaction = buildTransaction(account);

    assertTrue(unlockAccount(account, password));
    final Transaction signed = signTransaction(account, transaction);
    assertTrue(lockAccount(account, password));

    final TxHash txHash = aergoClient.getTransactionOperation().commit(signed);
    logger.info("TxHash: {}", txHash);

    waitForNextBlockToGenerate();

    final Transaction queried = aergoClient.getTransactionOperation().getTransaction(txHash);
    logger.info("Queired transaction: {}", queried);

    assertTrue(queried.isConfirmed());
  }

  @Test
  public void testSendTx() throws Exception {
    final String password = randomUUID().toString();
    final Account account = createServerAccount(password);

    final Account recipient = createClientAccount();
    final AccountState preState = aergoClient.getAccountOperation().getState(recipient);
    final Aer balance = preState.getBalance();

    final Aer amount = Aer.of("100", Unit.AER);
    assertTrue(unlockAccount(account, password));
    aergoClient.getTransactionOperation().send(account.getAddress(), recipient.getAddress(),
        amount);
    assertTrue(lockAccount(account, password));

    waitForNextBlockToGenerate();

    final AccountState postState = aergoClient.getAccountOperation().getState(recipient);
    assertEquals(balance.add(amount), postState.getBalance());
  }

  @Test
  public void shouldNotConfirmedForLowNonce() throws Exception {
    final Account account = createClientAccount();

    final RawTransaction transaction = RawTransaction.newBuilder()
        .sender(account)
        .recipient(recipient)
        .amount("10", Unit.AER)
        .nonce(0L)
        .build();
    final Transaction signed = signTransaction(account, transaction);

    try {
      aergoClient.getTransactionOperation().commit(signed);
      fail();
    } catch (CommitException e) {
      assertEquals(CommitException.CommitStatus.NONCE_TOO_LOW, e.getCommitStatus());
    }
  }
}
