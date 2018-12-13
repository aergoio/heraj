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
import hera.api.model.Aer;
import hera.api.model.BytesValue;
import hera.api.model.RawTransaction;
import hera.api.model.Signature;
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
public class TransactionBaseTemplateTest extends AbstractTestCase {

  protected static final AccountAddress ACCOUNT_ADDRESS =
      new AccountAddress(of(new byte[] {AccountAddress.VERSION}));

  protected final AccountAddress accountAddress =
      new AccountAddress(of(new byte[] {AccountAddress.VERSION}));

  @Override
  public void setUp() {
    super.setUp();
  }

  protected TransactionBaseTemplate supplyTransactionBaseTemplate(
      final AergoRPCServiceFutureStub aergoService) {
    final TransactionBaseTemplate transactionBaseTemplate = new TransactionBaseTemplate();
    transactionBaseTemplate.aergoService = aergoService;
    return transactionBaseTemplate;
  }

  @Test
  public void testGetTransactionInBlock() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture =
        service.submit(() -> Blockchain.TxInBlock.newBuilder().build());
    when(aergoService.getBlockTX(any())).thenReturn(mockListenableFuture);

    final TransactionBaseTemplate transactionBaseTemplate =
        supplyTransactionBaseTemplate(aergoService);

    final ResultOrErrorFuture<Transaction> transaction =
        transactionBaseTemplate.getTransactionFunction()
            .apply(new TxHash(of(randomUUID().toString().getBytes())));
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

    final TransactionBaseTemplate transactionBaseTemplate =
        supplyTransactionBaseTemplate(aergoService);

    final ResultOrErrorFuture<Transaction> transaction =
        transactionBaseTemplate.getTransactionFunction()
            .apply(new TxHash(of(randomUUID().toString().getBytes())));
    assertTrue(transaction.get().hasResult());
  }

  @Test
  public void testCommit() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = service.submit(() -> Rpc.CommitResultList.newBuilder()
        .addResults(Rpc.CommitResult.newBuilder().build()).build());
    when(aergoService.commitTX(any())).thenReturn(mockListenableFuture);

    final TransactionBaseTemplate transactionBaseTemplate =
        supplyTransactionBaseTemplate(aergoService);

    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .sender(ACCOUNT_ADDRESS)
        .recipient(ACCOUNT_ADDRESS)
        .amount("1000")
        .nonce(1L)
        .build();
    final Transaction transaction = new Transaction(rawTransaction, Signature.of(BytesValue.EMPTY),
        TxHash.of(BytesValue.EMPTY), null, 0, false);

    final ResultOrErrorFuture<TxHash> txHash =
        transactionBaseTemplate.getCommitFunction().apply(transaction);
    assertTrue(txHash.get().hasResult());
  }

  @Test
  public void testSend() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture =
        service.submit(() -> Rpc.CommitResult.newBuilder().build());
    when(aergoService.sendTX(any())).thenReturn(mockListenableFuture);

    final TransactionBaseTemplate transactionBaseTemplate =
        supplyTransactionBaseTemplate(aergoService);

    final ResultOrErrorFuture<TxHash> txHash = transactionBaseTemplate.getSendFunction()
        .apply(accountAddress, accountAddress, Aer.of("10 aer"));
    assertTrue(txHash.get().hasResult());
  }

}

