/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import hera.AbstractTestCase;
import hera.api.TransactionEitherOperation;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.tupleorerror.ResultOrError;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceBlockingStub;
import types.Blockchain;
import types.Rpc.CommitResult;
import types.Rpc.VerifyResult;

@SuppressWarnings("unchecked")
@PrepareForTest({AergoRPCServiceBlockingStub.class, Blockchain.Tx.class, VerifyResult.class,
    CommitResult.class})
public class TransactionTemplateTest extends AbstractTestCase {

  @Test
  public void testGetTransaction() throws Exception {
    ResultOrError<Transaction> eitherMock = mock(ResultOrError.class);
    when(eitherMock.getResult()).thenReturn(mock(Transaction.class));
    TransactionEitherOperation eitherOperationMock = mock(TransactionEitherOperation.class);
    when(eitherOperationMock.getTransaction(any())).thenReturn(eitherMock);

    final TransactionTemplate transactionTemplate =
        new TransactionTemplate(eitherOperationMock);

    final Transaction transaction =
        transactionTemplate.getTransaction(new TxHash(of(randomUUID().toString().getBytes())));
    assertNotNull(transaction);
  }

  @Test
  public void testCommit() throws Exception {
    ResultOrError<TxHash> eitherMock = mock(ResultOrError.class);
    when(eitherMock.getResult()).thenReturn(mock(TxHash.class));
    TransactionEitherOperation eitherOperationMock = mock(TransactionEitherOperation.class);
    when(eitherOperationMock.commit(any(Transaction.class))).thenReturn(eitherMock);

    final TransactionTemplate transactionTemplate =
        new TransactionTemplate(eitherOperationMock);

    final TxHash txHash = transactionTemplate.commit(new Transaction());
    assertNotNull(txHash);
  }

  @Test
  public void testSend() throws Exception {
    ResultOrError<TxHash> eitherMock = mock(ResultOrError.class);
    when(eitherMock.getResult()).thenReturn(mock(TxHash.class));
    TransactionEitherOperation eitherOperationMock = mock(TransactionEitherOperation.class);
    when(eitherOperationMock.send(any(Transaction.class))).thenReturn(eitherMock);

    final TransactionTemplate transactionTemplate =
        new TransactionTemplate(eitherOperationMock);

    final TxHash txHash = transactionTemplate.send(new Transaction());
    assertNotNull(txHash);
  }

}
