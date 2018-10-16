/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static com.google.protobuf.ByteString.copyFrom;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.util.concurrent.ListenableFuture;
import hera.AbstractTestCase;
import hera.Context;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.tupleorerror.ResultOrErrorFuture;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain;
import types.Rpc;

@SuppressWarnings({"rawtypes", "unchecked"})
@PrepareForTest({AergoRPCServiceFutureStub.class})
public class SignAsyncTemplateTest extends AbstractTestCase {

  protected static final Context context = AergoClientBuilder.getDefaultContext();

  protected SignAsyncTemplate supplySignAsyncTemplate(
      final AergoRPCServiceFutureStub aergoService) {
    final SignAsyncTemplate signAsyncTemplate = new SignAsyncTemplate();
    signAsyncTemplate.setContext(AergoClientBuilder.getDefaultContext());
    signAsyncTemplate.aergoService = aergoService;
    return signAsyncTemplate;
  }

  @Test
  public void testSign() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = service.submit(() -> Blockchain.Tx.newBuilder()
        .setBody(
            Blockchain.TxBody.newBuilder().setSign(copyFrom(randomUUID().toString().getBytes())))
        .setHash(copyFrom(randomUUID().toString().getBytes())).build());
    when(aergoService.signTX(any())).thenReturn(mockListenableFuture);

    final SignAsyncTemplate signAsyncTemplate = supplySignAsyncTemplate(aergoService);

    final ResultOrErrorFuture<Signature> signature =
        signAsyncTemplate.sign(null, new Transaction());
    assertTrue(signature.get().hasResult());
  }

  @Test
  public void testVerify() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = service.submit(
        () -> Rpc.VerifyResult.newBuilder().setError(Rpc.VerifyStatus.VERIFY_STATUS_OK).build());
    when(aergoService.verifyTX(any())).thenReturn(mockListenableFuture);

    final SignAsyncTemplate signAsyncTemplate = supplySignAsyncTemplate(aergoService);

    final ResultOrErrorFuture<Boolean> verifyResult =
        signAsyncTemplate.verify(null, new Transaction());
    assertTrue(verifyResult.get().hasResult());
  }

}
