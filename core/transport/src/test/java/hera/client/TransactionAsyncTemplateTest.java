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
import hera.api.model.AccountAddress;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.tupleorerror.ResultOrErrorFuture;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain;
import types.Rpc;

@SuppressWarnings({"rawtypes", "unchecked"})
@PrepareForTest({AergoRPCServiceFutureStub.class})
public class TransactionAsyncTemplateTest extends AbstractTestCase {

  protected final byte[] rawTxHash = randomUUID().toString().getBytes();

  protected final AccountAddress accountAddress =
      new AccountAddress(of(new byte[] {AccountAddress.VERSION}));

  @Override
  public void setUp() {
    super.setUp();
  }

  protected TransactionAsyncTemplate supplyTransactionAsyncTemplate(
      final AergoRPCServiceFutureStub aergoService) {
    final TransactionAsyncTemplate transactionAsyncTemplate = new TransactionAsyncTemplate();
    transactionAsyncTemplate.setContext(context);
    transactionAsyncTemplate.aergoService = aergoService;
    return transactionAsyncTemplate;
  }

  @Test
  public void testGetTransactionInBlock() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture =
        service.submit(() -> Blockchain.TxInBlock.newBuilder().build());
    when(aergoService.getBlockTX(any())).thenReturn(mockListenableFuture);

    final TransactionAsyncTemplate transactionAsyncTemplate =
        supplyTransactionAsyncTemplate(aergoService);

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

    final TransactionAsyncTemplate transactionAsyncTemplate =
        supplyTransactionAsyncTemplate(aergoService);

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

    final TransactionAsyncTemplate transactionAsyncTemplate =
        supplyTransactionAsyncTemplate(aergoService);

    final ResultOrErrorFuture<TxHash> txHash = transactionAsyncTemplate.commit(new Transaction());
    assertTrue(txHash.get().hasResult());
  }

  @Test
  public void testSend() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture =
        service.submit(() -> Rpc.CommitResult.newBuilder().build());
    when(aergoService.sendTX(any())).thenReturn(mockListenableFuture);

    final TransactionAsyncTemplate transactionAsyncTemplate =
        supplyTransactionAsyncTemplate(aergoService);

    final ResultOrErrorFuture<TxHash> txHash =
        transactionAsyncTemplate.send(accountAddress, accountAddress, 10);
    assertTrue(txHash.get().hasResult());
  }

}

