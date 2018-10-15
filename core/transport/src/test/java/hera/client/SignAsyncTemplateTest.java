/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static com.google.protobuf.ByteString.copyFrom;

import com.google.common.util.concurrent.ListenableFuture;
import hera.AbstractTestCase;
import hera.Context;
import hera.api.model.Signature;
import hera.api.model.Transaction;
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
@PrepareForTest({AergoRPCServiceFutureStub.class, Blockchain.Tx.class, VerifyResult.class,
    CommitResult.class})
public class SignAsyncTemplateTest extends AbstractTestCase {

  protected static final Context context = AergoClientBuilder.getDefaultContext();

  protected static final ModelConverter<Transaction, Blockchain.Tx> transactionConverter =
      mock(ModelConverter.class);

  @BeforeClass
  public static void setUpBeforeClass() {
    when(transactionConverter.convertToRpcModel(any(Transaction.class)))
        .thenReturn(mock(Blockchain.Tx.class));
    when(transactionConverter.convertToDomainModel(any(Blockchain.Tx.class)))
        .thenReturn(mock(Transaction.class));
  }

  @Test
  public void testSign() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = service.submit(() -> Blockchain.Tx.newBuilder()
        .setBody(
            Blockchain.TxBody.newBuilder().setSign(copyFrom(randomUUID().toString().getBytes())))
        .setHash(copyFrom(randomUUID().toString().getBytes())).build());
    when(aergoService.signTX(any())).thenReturn(mockListenableFuture);

    final SignAsyncTemplate signAsyncTemplate =
        new SignAsyncTemplate(aergoService, context, transactionConverter);

    final ResultOrErrorFuture<Signature> signature = signAsyncTemplate.sign(null, new Transaction());
    assertTrue(signature.get().hasResult());
  }

  @Test
  public void testVerify() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = service.submit(
        () -> Rpc.VerifyResult.newBuilder().setError(Rpc.VerifyStatus.VERIFY_STATUS_OK).build());
    when(aergoService.verifyTX(any())).thenReturn(mockListenableFuture);

    final SignAsyncTemplate signAsyncTemplate =
        new SignAsyncTemplate(aergoService, context, transactionConverter);

    final ResultOrErrorFuture<Boolean> verifyResult = signAsyncTemplate.verify(null, new Transaction());
    assertTrue(verifyResult.get().hasResult());
  }

}
