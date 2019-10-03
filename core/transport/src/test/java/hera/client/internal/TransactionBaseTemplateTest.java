/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.internal;

import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.util.concurrent.ListenableFuture;
import hera.AbstractTestCase;
import hera.ThreadLocalContextProvider;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import java.util.concurrent.Callable;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain;
import types.Rpc;

@PrepareForTest({AergoRPCServiceFutureStub.class})
public class TransactionBaseTemplateTest extends AbstractTestCase {

  @Override
  public void setUp() {
    super.setUp();
  }

  protected TransactionBaseTemplate supplyTransactionBaseTemplate(
      final AergoRPCServiceFutureStub aergoService) {
    final TransactionBaseTemplate transactionBaseTemplate = new TransactionBaseTemplate();
    transactionBaseTemplate.aergoService = aergoService;
    transactionBaseTemplate.contextProvider = new ThreadLocalContextProvider(context, this);
    return transactionBaseTemplate;
  }

  @Test
  public void testGetTransactionInBlock() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<Blockchain.Tx> txListenableFuture =
        service.submit(new Callable<Blockchain.Tx>() {
          @Override
          public Blockchain.Tx call() throws Exception {
            throw new UnsupportedOperationException();
          }
        });
    ListenableFuture<Blockchain.TxInBlock> txInBlockListenableFuture =
        service.submit(new Callable<Blockchain.TxInBlock>() {
          @Override
          public Blockchain.TxInBlock call() throws Exception {
            return Blockchain.TxInBlock.newBuilder().build();
          }
        });
    when(aergoService.getTX(any(Rpc.SingleBytes.class))).thenReturn(txListenableFuture);
    when(aergoService.getBlockTX(any(Rpc.SingleBytes.class))).thenReturn(txInBlockListenableFuture);

    final TransactionBaseTemplate transactionBaseTemplate =
        supplyTransactionBaseTemplate(aergoService);

    final FinishableFuture<Transaction> transaction =
        transactionBaseTemplate.getTransactionFunction()
            .apply(new TxHash(of(randomUUID().toString().getBytes())));
    assertNotNull(transaction.get());
  }

  @Test
  public void testGetTransactionInMempool() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<Blockchain.Tx> mockListenableFuture =
        service.submit(new Callable<Blockchain.Tx>() {
          @Override
          public Blockchain.Tx call() throws Exception {
            return Blockchain.Tx.newBuilder().build();
          }
        });
    when(aergoService.getTX(any(Rpc.SingleBytes.class))).thenReturn(mockListenableFuture);

    final TransactionBaseTemplate transactionBaseTemplate =
        supplyTransactionBaseTemplate(aergoService);

    final FinishableFuture<Transaction> transaction =
        transactionBaseTemplate.getTransactionFunction()
            .apply(new TxHash(of(randomUUID().toString().getBytes())));
    assertNotNull(transaction.get());
  }

  @Test
  public void testCommit() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<Rpc.CommitResultList> mockListenableFuture =
        service.submit(new Callable<Rpc.CommitResultList>() {
          @Override
          public Rpc.CommitResultList call() throws Exception {
            return Rpc.CommitResultList.newBuilder()
                .addResults(Rpc.CommitResult.newBuilder().build()).build();
          }
        });
    when(aergoService.commitTX(any(Blockchain.TxList.class))).thenReturn(mockListenableFuture);

    final TransactionBaseTemplate transactionBaseTemplate =
        supplyTransactionBaseTemplate(aergoService);

    final RawTransaction rawTransaction = RawTransaction.newBuilder(chainIdHash)
        .from(accountAddress)
        .to(accountAddress)
        .amount("1000", Unit.AER)
        .nonce(1L)
        .build();
    final Transaction transaction = Transaction.newBuilder()
        .rawTransaction(rawTransaction)
        .build();

    final FinishableFuture<TxHash> txHash =
        transactionBaseTemplate.getCommitFunction().apply(transaction);
    assertNotNull(txHash.get());
  }

  @Test
  public void testSend() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<Rpc.CommitResult> mockListenableFuture =
        service.submit(new Callable<Rpc.CommitResult>() {
          @Override
          public Rpc.CommitResult call() throws Exception {
            return Rpc.CommitResult.newBuilder().build();
          }
        });
    when(aergoService.sendTX(any(Blockchain.Tx.class))).thenReturn(mockListenableFuture);

    final TransactionBaseTemplate transactionBaseTemplate =
        supplyTransactionBaseTemplate(aergoService);

    final FinishableFuture<TxHash> txHash = transactionBaseTemplate.getSendFunction()
        .apply(accountAddress, accountAddress, Aer.of("10", Unit.AER));
    assertNotNull(txHash.get());
  }

}

