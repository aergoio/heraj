/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import hera.AbstractTestCase;
import hera.api.TransactionAsyncOperation;
import hera.api.model.Hash;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
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
    CompletableFuture<Optional<Transaction>> futureMock = mock(CompletableFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(Optional.empty());
    TransactionAsyncOperation asyncOperationMock = mock(TransactionAsyncOperation.class);
    when(asyncOperationMock.getTransaction(any(Hash.class))).thenReturn(futureMock);

    final TransactionTemplate transactionTemplate = new TransactionTemplate(asyncOperationMock);

    final Optional<Transaction> transaction = transactionTemplate
        .getTransaction(new Hash(randomUUID().toString().getBytes()));
    assertNotNull(transaction);
  }

  @Test
  public void testSign() throws Exception {
    CompletableFuture<Signature> futureMock = mock(CompletableFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(Signature.class));
    TransactionAsyncOperation asyncOperationMock = mock(TransactionAsyncOperation.class);
    when(asyncOperationMock.sign(any(Transaction.class))).thenReturn(futureMock);

    final TransactionTemplate transactionTemplate = new TransactionTemplate(asyncOperationMock);

    final Signature signature = transactionTemplate.sign(new Transaction());
    assertNotNull(signature);
  }

  @Test
  public void testVerify() throws Exception {
    CompletableFuture<Boolean> futureMock = mock(CompletableFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(true);
    TransactionAsyncOperation asyncOperationMock = mock(TransactionAsyncOperation.class);
    when(asyncOperationMock.verify(any(Transaction.class))).thenReturn(futureMock);

    final TransactionTemplate transactionTemplate = new TransactionTemplate(asyncOperationMock);

    boolean verifyResult = transactionTemplate.verify(new Transaction());
    assertTrue(verifyResult);
  }

  @Test
  public void testCommit() throws Exception {
    CompletableFuture<Optional<Hash>> futureMock = mock(CompletableFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(Optional.empty());
    TransactionAsyncOperation asyncOperationMock = mock(TransactionAsyncOperation.class);
    when(asyncOperationMock.commit(any(Transaction.class))).thenReturn(futureMock);

    final TransactionTemplate transactionTemplate = new TransactionTemplate(asyncOperationMock);

    final Optional<Hash> hash = transactionTemplate.commit(new Transaction());
    assertNotNull(hash);
  }

}
