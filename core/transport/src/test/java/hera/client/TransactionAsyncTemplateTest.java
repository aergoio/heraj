/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.TRANSACTION_COMMIT_ASYNC;
import static hera.TransportConstants.TRANSACTION_GETTX_ASYNC;
import static hera.TransportConstants.TRANSACTION_SEND_ASYNC;
import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.ContextProvider;
import hera.api.model.AccountAddress;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.api.tupleorerror.WithIdentity;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

@PrepareForTest({TransactionBaseTemplate.class})
public class TransactionAsyncTemplateTest extends AbstractTestCase {

  protected final byte[] rawTxHash = randomUUID().toString().getBytes();

  protected final AccountAddress accountAddress =
      new AccountAddress(of(new byte[] {AccountAddress.VERSION}));

  @Override
  public void setUp() {
    super.setUp();
  }

  protected TransactionAsyncTemplate supplyTransactionAsyncTemplate(
      final TransactionBaseTemplate transactionBaseTemplate) {
    final TransactionAsyncTemplate transactionAsyncTemplate = new TransactionAsyncTemplate();
    transactionAsyncTemplate.transactionBaseTemplate = transactionBaseTemplate;
    transactionAsyncTemplate.setContextProvider(ContextProvider.defaultProvider);
    return transactionAsyncTemplate;
  }

  @Test
  public void testGetTransaction() {
    final TransactionBaseTemplate base = mock(TransactionBaseTemplate.class);
    ResultOrErrorFuture<Transaction> future =
        ResultOrErrorFutureFactory.supply(() -> new Transaction());
    when(base.getTransactionFunction()).thenReturn(h -> future);

    final TransactionAsyncTemplate transactionAsyncTemplate =
        supplyTransactionAsyncTemplate(base);

    final ResultOrErrorFuture<Transaction> transaction =
        transactionAsyncTemplate.getTransaction(new TxHash(of(randomUUID().toString().getBytes())));
    assertTrue(transaction.get().hasResult());
    assertEquals(TRANSACTION_GETTX_ASYNC,
        ((WithIdentity) transactionAsyncTemplate.getTransactionFunction()).getIdentity());
  }

  @Test
  public void testCommit() {
    final TransactionBaseTemplate base = mock(TransactionBaseTemplate.class);
    ResultOrErrorFuture<TxHash> future =
        ResultOrErrorFutureFactory.supply(() -> new TxHash(of(randomUUID().toString().getBytes())));
    when(base.getCommitFunction()).thenReturn(t -> future);

    final TransactionAsyncTemplate transactionAsyncTemplate =
        supplyTransactionAsyncTemplate(base);

    final ResultOrErrorFuture<TxHash> txHash = transactionAsyncTemplate.commit(new Transaction());
    assertTrue(txHash.get().hasResult());
    assertEquals(TRANSACTION_COMMIT_ASYNC,
        ((WithIdentity) transactionAsyncTemplate.getCommitFunction()).getIdentity());
  }

  @Test
  public void testSend() {
    final TransactionBaseTemplate base = mock(TransactionBaseTemplate.class);
    ResultOrErrorFuture<TxHash> future =
        ResultOrErrorFutureFactory.supply(() -> new TxHash(of(randomUUID().toString().getBytes())));
    when(base.getSendFunction()).thenReturn((a, r, m) -> future);

    final TransactionAsyncTemplate transactionAsyncTemplate =
        supplyTransactionAsyncTemplate(base);

    final ResultOrErrorFuture<TxHash> txHash =
        transactionAsyncTemplate.send(accountAddress, accountAddress, 10);
    assertTrue(txHash.get().hasResult());
    assertEquals(TRANSACTION_SEND_ASYNC,
        ((WithIdentity) transactionAsyncTemplate.getSendFunction()).getIdentity());
  }

}

