/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.util.concurrent.ListenableFuture;
import hera.AbstractTestCase;
import hera.api.model.Hash;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.transport.ModelConverter;
import java.util.Optional;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain;
import types.Rpc.CommitResult;
import types.Rpc.VerifyResult;

@PrepareForTest({AergoRPCServiceFutureStub.class, Blockchain.Tx.class, VerifyResult.class,
    CommitResult.class})
public class TransactionAsyncTemplateTest extends AbstractTestCase {

  protected final byte[] TXHASH = randomUUID().toString().getBytes();

  protected static final ModelConverter<Transaction, Blockchain.Tx> converter = mock(
      ModelConverter.class);

  @BeforeClass
  public static void setUpBeforeClass() {
    when(converter.convertToRpcModel(any(Transaction.class))).thenReturn(mock(Blockchain.Tx.class));
    when(converter.convertToDomainModel(any(Blockchain.Tx.class)))
        .thenReturn(mock(Transaction.class));
  }

  @Test
  public void testGetTransaction() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = mock(ListenableFuture.class);
    when(aergoService.getTX(any())).thenReturn(mockListenableFuture);

    final TransactionAsyncTemplate transactionAsyncTemplate = new TransactionAsyncTemplate(
        aergoService, converter);

    final ResultOrErrorFuture<Optional<Transaction>> transaction = transactionAsyncTemplate
        .getTransaction(new Hash(randomUUID().toString().getBytes()));
    assertNotNull(transaction);
  }

  @Test
  public void testSign() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = mock(ListenableFuture.class);
    when(aergoService.signTX(any())).thenReturn(mockListenableFuture);

    final TransactionAsyncTemplate transactionAsyncTemplate = new TransactionAsyncTemplate(
        aergoService, converter);

    final ResultOrErrorFuture<Signature> signature = transactionAsyncTemplate
        .sign(new Transaction());
    assertNotNull(signature);
  }

  @Test
  public void testVerify() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = mock(ListenableFuture.class);
    when(aergoService.verifyTX(any())).thenReturn(mockListenableFuture);

    final TransactionAsyncTemplate transactionAsyncTemplate = new TransactionAsyncTemplate(
        aergoService, converter);

    final ResultOrErrorFuture<Boolean> verifyResult = transactionAsyncTemplate
        .verify(new Transaction());
    assertNotNull(verifyResult);
  }

  @Test
  public void testCommit() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = mock(ListenableFuture.class);
    when(aergoService.commitTX(any())).thenReturn(mockListenableFuture);

    final TransactionAsyncTemplate transactionAsyncTemplate = new TransactionAsyncTemplate(
        aergoService, converter);

    final ResultOrErrorFuture<Optional<Hash>> hash = transactionAsyncTemplate
        .commit(new Transaction());
    assertNotNull(hash);
  }

}

