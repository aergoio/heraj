/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import hera.AbstractTestCase;
import hera.api.model.AccountAddress;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.tupleorerror.ResultOrError;
import hera.api.tupleorerror.ResultOrErrorFuture;
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

  protected final AccountAddress accountAddress =
      new AccountAddress(of(new byte[] {AccountAddress.VERSION}));

  @Override
  public void setUp() {
    super.setUp();
  }

  @Test
  public void testGetTransaction() {
    ResultOrErrorFuture<Transaction> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get()).thenReturn(mock(ResultOrError.class));
    TransactionAsyncTemplate asyncOperationMock = mock(TransactionAsyncTemplate.class);
    when(asyncOperationMock.getTransaction(any())).thenReturn(futureMock);

    final TransactionEitherTemplate transactionTemplate = new TransactionEitherTemplate();
    transactionTemplate.transactionAsyncOperation = asyncOperationMock;

    final ResultOrError<Transaction> transaction =
        transactionTemplate.getTransaction(new TxHash(of(randomUUID().toString().getBytes())));
    assertNotNull(transaction);
  }

  @Test
  public void testCommit() {
    ResultOrErrorFuture<TxHash> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get()).thenReturn(mock(ResultOrError.class));
    TransactionAsyncTemplate asyncOperationMock = mock(TransactionAsyncTemplate.class);
    when(asyncOperationMock.commit(any(Transaction.class))).thenReturn(futureMock);

    final TransactionEitherTemplate transactionTemplate = new TransactionEitherTemplate();
    transactionTemplate.transactionAsyncOperation = asyncOperationMock;

    final ResultOrError<TxHash> txHash = transactionTemplate.commit(new Transaction());
    assertNotNull(txHash);
  }

  @Test
  public void testSend() {
    ResultOrErrorFuture<TxHash> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get()).thenReturn(mock(ResultOrError.class));
    TransactionAsyncTemplate asyncOperationMock = mock(TransactionAsyncTemplate.class);
    when(asyncOperationMock.send(any(AccountAddress.class), any(), anyLong()))
        .thenReturn(futureMock);

    final TransactionEitherTemplate transactionTemplate = new TransactionEitherTemplate();
    transactionTemplate.transactionAsyncOperation = asyncOperationMock;

    final ResultOrError<TxHash> txHash =
        transactionTemplate.send(accountAddress, accountAddress, 10);
    assertNotNull(txHash);
  }

}
