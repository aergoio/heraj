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
import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.api.tupleorerror.WithIdentity;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

@PrepareForTest({TransactionBaseTemplate.class})
public class TransactionTemplateTest extends AbstractTestCase {

  protected final byte[] rawTxHash = randomUUID().toString().getBytes();

  protected final AccountAddress accountAddress =
      new AccountAddress(of(new byte[] {AccountAddress.VERSION}));

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
    ResultOrErrorFuture<Transaction> future =
        ResultOrErrorFutureFactory.supply(() -> mockTransaction);
    when(base.getTransactionFunction()).thenReturn(h -> future);

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
    ResultOrErrorFuture<TxHash> future =
        ResultOrErrorFutureFactory.supply(() -> new TxHash(of(randomUUID().toString().getBytes())));
    when(base.getCommitFunction()).thenReturn(t -> future);

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
    ResultOrErrorFuture<TxHash> future =
        ResultOrErrorFutureFactory.supply(() -> new TxHash(of(randomUUID().toString().getBytes())));
    when(base.getSendFunction()).thenReturn((a, r, m) -> future);

    final TransactionTemplate transactionTemplate =
        supplyTransactionTemplate(base);

    final TxHash txHash =
        transactionTemplate.send(accountAddress, accountAddress, Aer.of("10 aer"));
    assertNotNull(txHash);
    assertEquals(TRANSACTION_SEND,
        ((WithIdentity) transactionTemplate.getSendFunction()).getIdentity());
  }

}

