/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.TRANSACTION_COMMIT_EITHER;
import static hera.TransportConstants.TRANSACTION_GETTX_EITHER;
import static hera.TransportConstants.TRANSACTION_SEND_EITHER;
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
import hera.api.tupleorerror.ResultOrError;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.api.tupleorerror.WithIdentity;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

@PrepareForTest({TransactionBaseTemplate.class})
public class TransactionEitherTemplateTest extends AbstractTestCase {

  protected final byte[] rawTxHash = randomUUID().toString().getBytes();

  protected final AccountAddress accountAddress =
      new AccountAddress(of(new byte[] {AccountAddress.VERSION}));

  @Override
  public void setUp() {
    super.setUp();
  }

  protected TransactionEitherTemplate supplyTransactionEitherTemplate(
      final TransactionBaseTemplate transactionBaseTemplate) {
    final TransactionEitherTemplate transactionEitherTemplate = new TransactionEitherTemplate();
    transactionEitherTemplate.transactionBaseTemplate = transactionBaseTemplate;
    transactionEitherTemplate.setContextProvider(ContextProvider.defaultProvider);
    return transactionEitherTemplate;
  }

  @Test
  public void testGetTransaction() {
    final TransactionBaseTemplate base = mock(TransactionBaseTemplate.class);
    ResultOrErrorFuture<Transaction> future =
        ResultOrErrorFutureFactory.supply(() -> new Transaction());
    when(base.getTransactionFunction()).thenReturn(h -> future);

    final TransactionEitherTemplate transactionEitherTemplate =
        supplyTransactionEitherTemplate(base);

    final ResultOrError<Transaction> transaction =
        transactionEitherTemplate
            .getTransaction(new TxHash(of(randomUUID().toString().getBytes())));
    assertTrue(transaction.hasResult());
    assertEquals(TRANSACTION_GETTX_EITHER,
        ((WithIdentity) transactionEitherTemplate.getTransactionFunction()).getIdentity());
  }

  @Test
  public void testCommit() {
    final TransactionBaseTemplate base = mock(TransactionBaseTemplate.class);
    ResultOrErrorFuture<TxHash> future =
        ResultOrErrorFutureFactory.supply(() -> new TxHash(of(randomUUID().toString().getBytes())));
    when(base.getCommitFunction()).thenReturn(t -> future);

    final TransactionEitherTemplate transactionEitherTemplate =
        supplyTransactionEitherTemplate(base);

    final ResultOrError<TxHash> txHash = transactionEitherTemplate.commit(new Transaction());
    assertTrue(txHash.hasResult());
    assertEquals(TRANSACTION_COMMIT_EITHER,
        ((WithIdentity) transactionEitherTemplate.getCommitFunction()).getIdentity());
  }

  @Test
  public void testSend() {
    final TransactionBaseTemplate base = mock(TransactionBaseTemplate.class);
    ResultOrErrorFuture<TxHash> future =
        ResultOrErrorFutureFactory.supply(() -> new TxHash(of(randomUUID().toString().getBytes())));
    when(base.getSendFunction()).thenReturn((a, r, m) -> future);

    final TransactionEitherTemplate transactionEitherTemplate =
        supplyTransactionEitherTemplate(base);

    final ResultOrError<TxHash> txHash =
        transactionEitherTemplate.send(accountAddress, accountAddress, 10);
    assertTrue(txHash.hasResult());
    assertEquals(TRANSACTION_SEND_EITHER,
        ((WithIdentity) transactionEitherTemplate.getSendFunction()).getIdentity());
  }

}

