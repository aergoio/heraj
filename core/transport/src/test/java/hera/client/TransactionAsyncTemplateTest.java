/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.util.concurrent.ListenableFuture;
import hera.AbstractTestCase;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.transport.ModelConverter;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain;
import types.Rpc.CommitResult;
import types.Rpc.VerifyResult;

@SuppressWarnings({"rawtypes", "unchecked"})
@PrepareForTest({AergoRPCServiceFutureStub.class, Blockchain.Tx.class, Blockchain.TxInBlock.class,
    VerifyResult.class, CommitResult.class})
public class TransactionAsyncTemplateTest extends AbstractTestCase {

  protected final byte[] TXHASH = randomUUID().toString().getBytes();

  protected static final ModelConverter<Transaction, Blockchain.Tx> transactionConverter =
      mock(ModelConverter.class);

  protected static final ModelConverter<Transaction, Blockchain.TxInBlock> transactionInBlockConverter =
      mock(ModelConverter.class);

  @BeforeClass
  public static void setUpBeforeClass() {
    when(transactionConverter.convertToRpcModel(any(Transaction.class)))
        .thenReturn(mock(Blockchain.Tx.class));
    when(transactionConverter.convertToDomainModel(any(Blockchain.Tx.class)))
        .thenReturn(mock(Transaction.class));
    when(transactionInBlockConverter.convertToRpcModel(any(Transaction.class)))
        .thenReturn(mock(Blockchain.TxInBlock.class));
    when(transactionInBlockConverter.convertToDomainModel(any(Blockchain.TxInBlock.class)))
        .thenReturn(mock(Transaction.class));
  }

  @Test
  public void testGetTransaction() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = mock(ListenableFuture.class);
    when(aergoService.getBlockTX(any())).thenReturn(mockListenableFuture);

    final TransactionAsyncTemplate transactionAsyncTemplate = new TransactionAsyncTemplate(
        aergoService, transactionConverter, transactionInBlockConverter);

    final ResultOrErrorFuture<Transaction> transaction =
        transactionAsyncTemplate.getTransaction(new TxHash(of(randomUUID().toString().getBytes())));
    assertNotNull(transaction);
  }

  @Test
  public void testCommit() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = mock(ListenableFuture.class);
    when(aergoService.commitTX(any())).thenReturn(mockListenableFuture);

    final TransactionAsyncTemplate transactionAsyncTemplate = new TransactionAsyncTemplate(
        aergoService, transactionConverter, transactionInBlockConverter);

    final ResultOrErrorFuture<TxHash> txHash = transactionAsyncTemplate.commit(new Transaction());
    assertNotNull(txHash);
  }

  @Test
  public void testSend() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = mock(ListenableFuture.class);
    when(aergoService.sendTX(any())).thenReturn(mockListenableFuture);

    final TransactionAsyncTemplate transactionAsyncTemplate = new TransactionAsyncTemplate(
        aergoService, transactionConverter, transactionInBlockConverter);

    final ResultOrErrorFuture<TxHash> txHash = transactionAsyncTemplate.send(new Transaction());
    assertNotNull(txHash);
  }

}

