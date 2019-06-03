/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.TRANSACTION_COMMIT;
import static hera.TransportConstants.TRANSACTION_GETTX;
import static hera.TransportConstants.TRANSACTION_SEND;
import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.ContextProvider;
import hera.api.function.Function1;
import hera.api.function.Function3;
import hera.api.function.WithIdentity;
import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.client.internal.FinishableFuture;
import hera.client.internal.TransactionBaseTemplate;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

@PrepareForTest({TransactionBaseTemplate.class})
public class TransactionTemplateTest extends AbstractTestCase {

  protected final byte[] rawTxHash = randomUUID().toString().getBytes();

  @Override
  public void setUp() {
    super.setUp();
  }

  protected TransactionTemplate supplyTransactionTemplate(
      final TransactionBaseTemplate transactionBaseTemplate) {
    final TransactionTemplate transactionTemplate = new TransactionTemplate();
    transactionTemplate.transactionBaseTemplate = transactionBaseTemplate;
    transactionTemplate.setContextProvider(ContextProvider.defaultProvider);
    return transactionTemplate;
  }

  @Test
  public void testGetTransaction() {
    final TransactionBaseTemplate base = mock(TransactionBaseTemplate.class);
    final Transaction mockTransaction = mock(Transaction.class);
    final FinishableFuture<Transaction> future = new FinishableFuture<Transaction>();
    future.success(mockTransaction);
    when(base.getTransactionFunction())
        .thenReturn(new Function1<TxHash, FinishableFuture<Transaction>>() {
          @Override
          public FinishableFuture<Transaction> apply(TxHash t) {
            return future;
          }
        });

    final TransactionTemplate transactionTemplate =
        supplyTransactionTemplate(base);

    final Transaction transaction =
        transactionTemplate
            .getTransaction(new TxHash(of(randomUUID().toString().getBytes())));
    assertNotNull(transaction);
    assertEquals(TRANSACTION_GETTX,
        ((WithIdentity) transactionTemplate.getTransactionFunction()).getIdentity());
  }

  @Test
  public void testCommit() {
    final TransactionBaseTemplate base = mock(TransactionBaseTemplate.class);
    final FinishableFuture<TxHash> future = new FinishableFuture<TxHash>();
    future.success(new TxHash(of(randomUUID().toString().getBytes())));
    when(base.getCommitFunction())
        .thenReturn(new Function1<Transaction, FinishableFuture<TxHash>>() {
          @Override
          public FinishableFuture<TxHash> apply(Transaction t) {
            return future;
          }
        });

    final TransactionTemplate transactionTemplate =
        supplyTransactionTemplate(base);

    final Transaction transaction = mock(Transaction.class);
    final TxHash txHash = transactionTemplate.commit(transaction);
    assertNotNull(txHash);
    assertEquals(TRANSACTION_COMMIT,
        ((WithIdentity) transactionTemplate.getCommitFunction()).getIdentity());
  }

  @Test
  public void testSend() {
    final TransactionBaseTemplate base = mock(TransactionBaseTemplate.class);
    final FinishableFuture<TxHash> future = new FinishableFuture<TxHash>();
    future.success(new TxHash(of(randomUUID().toString().getBytes())));
    when(base.getSendFunction())
        .thenReturn(new Function3<AccountAddress, AccountAddress, Aer, FinishableFuture<TxHash>>() {
          @Override
          public FinishableFuture<TxHash> apply(AccountAddress t1, AccountAddress t2, Aer t3) {
            return future;
          }
        });

    final TransactionTemplate transactionTemplate =
        supplyTransactionTemplate(base);

    final TxHash txHash =
        transactionTemplate.send(accountAddress, accountAddress, Aer.of("10", Unit.AER));
    assertNotNull(txHash);
    assertEquals(TRANSACTION_SEND,
        ((WithIdentity) transactionTemplate.getSendFunction()).getIdentity());
  }

}

