/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.util.concurrent.ListenableFuture;
import hera.AbstractTestCase;
import hera.Context;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.transport.ModelConverter;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain;
import types.Rpc;
import types.Rpc.CommitResult;
import types.Rpc.VerifyResult;

@SuppressWarnings({"rawtypes", "unchecked"})
@PrepareForTest({AergoRPCServiceFutureStub.class, Blockchain.Tx.class, Blockchain.TxInBlock.class,
    VerifyResult.class, CommitResult.class})
public class TransactionAsyncTemplateTest extends AbstractTestCase {

  protected final byte[] TXHASH = randomUUID().toString().getBytes();

  protected static final Context context = AergoClientBuilder.getDefaultContext();

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
  public void testGetTransactionInBlock() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture =
        service.submit(() -> Blockchain.TxInBlock.newBuilder().build());
    when(aergoService.getBlockTX(any())).thenReturn(mockListenableFuture);

    final TransactionAsyncTemplate transactionAsyncTemplate = new TransactionAsyncTemplate(
        aergoService, context, transactionConverter, transactionInBlockConverter);

    final ResultOrErrorFuture<Transaction> transaction =
        transactionAsyncTemplate.getTransaction(new TxHash(of(randomUUID().toString().getBytes())));
    assertTrue(transaction.get().hasResult());
  }

  @Test
  public void testGetTransactionInMemory() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture txInBlockListenableFuture = service.submit(() -> {
      throw new UnsupportedOperationException();
    });
    ListenableFuture txListenableFuture = service.submit(() -> Blockchain.Tx.newBuilder().build());
    when(aergoService.getBlockTX(any())).thenReturn(txInBlockListenableFuture);
    when(aergoService.getTX(any())).thenReturn(txListenableFuture);

    final TransactionAsyncTemplate transactionAsyncTemplate = new TransactionAsyncTemplate(
        aergoService, context, transactionConverter, transactionInBlockConverter);

    final ResultOrErrorFuture<Transaction> transaction =
        transactionAsyncTemplate.getTransaction(new TxHash(of(randomUUID().toString().getBytes())));
    assertTrue(transaction.get().hasResult());
  }

  @Test
  public void testCommit() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = service.submit(() -> Rpc.CommitResultList.newBuilder()
        .addResults(Rpc.CommitResult.newBuilder().build()).build());
    when(aergoService.commitTX(any())).thenReturn(mockListenableFuture);

    final TransactionAsyncTemplate transactionAsyncTemplate = new TransactionAsyncTemplate(
        aergoService, context, transactionConverter, transactionInBlockConverter);

    final ResultOrErrorFuture<TxHash> txHash = transactionAsyncTemplate.commit(new Transaction());
    assertTrue(txHash.get().hasResult());
  }

  @Test
  public void testSend() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture =
        service.submit(() -> Rpc.CommitResult.newBuilder().build());
    when(aergoService.sendTX(any())).thenReturn(mockListenableFuture);

    final TransactionAsyncTemplate transactionAsyncTemplate = new TransactionAsyncTemplate(
        aergoService, context, transactionConverter, transactionInBlockConverter);

    final ResultOrErrorFuture<TxHash> txHash = transactionAsyncTemplate.send(new Transaction());
    assertTrue(txHash.get().hasResult());
  }

}

