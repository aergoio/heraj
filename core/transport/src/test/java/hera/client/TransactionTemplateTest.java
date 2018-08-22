/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import hera.AbstractTestCase;
import hera.api.TransactionAsyncOperation;
import hera.api.model.Hash;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.tupleorerror.ResultOrError;
import hera.api.tupleorerror.ResultOrErrorFuture;
import java.util.Optional;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceBlockingStub;
import types.Blockchain;
import types.Rpc.CommitResult;
import types.Rpc.VerifyResult;

@PrepareForTest({AergoRPCServiceBlockingStub.class, Blockchain.Tx.class, VerifyResult.class,
    CommitResult.class})
public class TransactionTemplateTest extends AbstractTestCase {

  @Test
  public void testGetTransaction() throws Exception {
    ResultOrErrorFuture<Optional<Transaction>> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(ResultOrError.class));
    TransactionAsyncOperation asyncOperationMock = mock(TransactionAsyncOperation.class);
    when(asyncOperationMock.getTransaction(any(Hash.class))).thenReturn(futureMock);

    final TransactionTemplate transactionTemplate = new TransactionTemplate(asyncOperationMock);

    final ResultOrError<Optional<Transaction>> transaction =
        transactionTemplate.getTransaction(new Hash(randomUUID().toString().getBytes()));
    assertNotNull(transaction);
  }

  @Test
  public void testSign() throws Exception {
    ResultOrErrorFuture<Signature> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(ResultOrError.class));
    TransactionAsyncOperation asyncOperationMock = mock(TransactionAsyncOperation.class);
    when(asyncOperationMock.sign(any(Transaction.class))).thenReturn(futureMock);

    final TransactionTemplate transactionTemplate = new TransactionTemplate(asyncOperationMock);

    final ResultOrError<Signature> signature = transactionTemplate.sign(new Transaction());
    assertNotNull(signature);
  }

  @Test
  public void testVerify() throws Exception {
    ResultOrErrorFuture<Boolean> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(ResultOrError.class));
    TransactionAsyncOperation asyncOperationMock = mock(TransactionAsyncOperation.class);
    when(asyncOperationMock.verify(any(Transaction.class))).thenReturn(futureMock);

    final TransactionTemplate transactionTemplate = new TransactionTemplate(asyncOperationMock);

    ResultOrError<Boolean> verifyResult = transactionTemplate.verify(new Transaction());
    assertNotNull(verifyResult);
  }

  @Test
  public void testCommit() throws Exception {
    ResultOrErrorFuture<Optional<Hash>> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(ResultOrError.class));
    TransactionAsyncOperation asyncOperationMock = mock(TransactionAsyncOperation.class);
    when(asyncOperationMock.commit(any(Transaction.class))).thenReturn(futureMock);

    final TransactionTemplate transactionTemplate = new TransactionTemplate(asyncOperationMock);

    final ResultOrError<Optional<Hash>> hash = transactionTemplate.commit(new Transaction());
    assertNotNull(hash);
  }

}
