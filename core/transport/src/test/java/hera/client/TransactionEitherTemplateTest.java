/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import hera.AbstractTestCase;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.tupleorerror.ResultOrError;
import hera.api.tupleorerror.ResultOrErrorFuture;
import java.util.concurrent.TimeoutException;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceBlockingStub;
import types.Blockchain;
import types.Rpc.CommitResult;
import types.Rpc.VerifyResult;

@SuppressWarnings("unchecked")
@PrepareForTest({AergoRPCServiceBlockingStub.class, Blockchain.Tx.class, VerifyResult.class,
    CommitResult.class})
public class TransactionEitherTemplateTest extends AbstractTestCase {

  @Test
  public void testGetTransaction() throws Exception {
    ResultOrErrorFuture<Transaction> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(ResultOrError.class));
    TransactionAsyncTemplate asyncOperationMock = mock(TransactionAsyncTemplate.class);
    when(asyncOperationMock.getTransaction(any())).thenReturn(futureMock);

    final TransactionEitherTemplate transactionTemplate = new TransactionEitherTemplate();
    transactionTemplate.transactionAsyncOperation = asyncOperationMock;

    final ResultOrError<Transaction> transaction =
        transactionTemplate.getTransaction(new TxHash(of(randomUUID().toString().getBytes())));
    assertNotNull(transaction);
  }

  @Test
  public void testGetTransactionWithTimeout() throws Exception {
    ResultOrErrorFuture<Transaction> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenThrow(TimeoutException.class);
    TransactionAsyncTemplate asyncOperationMock = mock(TransactionAsyncTemplate.class);
    when(asyncOperationMock.getTransaction(any())).thenReturn(futureMock);

    final TransactionEitherTemplate transactionTemplate = new TransactionEitherTemplate();
    transactionTemplate.transactionAsyncOperation = asyncOperationMock;

    final ResultOrError<Transaction> transaction =
        transactionTemplate.getTransaction(new TxHash(of(randomUUID().toString().getBytes())));
    assertTrue(transaction.hasError());
  }

  @Test
  public void testCommit() throws Exception {
    ResultOrErrorFuture<TxHash> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(ResultOrError.class));
    TransactionAsyncTemplate asyncOperationMock = mock(TransactionAsyncTemplate.class);
    when(asyncOperationMock.commit(any(Transaction.class))).thenReturn(futureMock);

    final TransactionEitherTemplate transactionTemplate = new TransactionEitherTemplate();
    transactionTemplate.transactionAsyncOperation = asyncOperationMock;

    final ResultOrError<TxHash> txHash = transactionTemplate.commit(new Transaction());
    assertNotNull(txHash);
  }

  @Test
  public void testCommitWithTimeout() throws Exception {
    ResultOrErrorFuture<TxHash> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenThrow(TimeoutException.class);
    TransactionAsyncTemplate asyncOperationMock = mock(TransactionAsyncTemplate.class);
    when(asyncOperationMock.commit(any(Transaction.class))).thenReturn(futureMock);

    final TransactionEitherTemplate transactionTemplate = new TransactionEitherTemplate();
    transactionTemplate.transactionAsyncOperation = asyncOperationMock;

    final ResultOrError<TxHash> txHash = transactionTemplate.commit(new Transaction());
    assertTrue(txHash.hasError());
  }

  @Test
  public void testSend() throws Exception {
    ResultOrErrorFuture<TxHash> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(ResultOrError.class));
    TransactionAsyncTemplate asyncOperationMock = mock(TransactionAsyncTemplate.class);
    when(asyncOperationMock.send(any(Transaction.class))).thenReturn(futureMock);

    final TransactionEitherTemplate transactionTemplate = new TransactionEitherTemplate();
    transactionTemplate.transactionAsyncOperation = asyncOperationMock;

    final ResultOrError<TxHash> txHash = transactionTemplate.send(new Transaction());
    assertNotNull(txHash);
  }

  @Test
  public void testSendWithTimeout() throws Exception {
    ResultOrErrorFuture<TxHash> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenThrow(TimeoutException.class);
    TransactionAsyncTemplate asyncOperationMock = mock(TransactionAsyncTemplate.class);
    when(asyncOperationMock.send(any(Transaction.class))).thenReturn(futureMock);

    final TransactionEitherTemplate transactionTemplate = new TransactionEitherTemplate();
    transactionTemplate.transactionAsyncOperation = asyncOperationMock;

    final ResultOrError<TxHash> txHash = transactionTemplate.send(new Transaction());
    assertTrue(txHash.hasError());
  }

}
