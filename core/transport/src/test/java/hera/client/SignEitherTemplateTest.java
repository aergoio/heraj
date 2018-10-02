/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import hera.AbstractTestCase;
import hera.api.SignAsyncOperation;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.tupleorerror.ResultOrError;
import hera.api.tupleorerror.ResultOrErrorFuture;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceBlockingStub;
import types.Blockchain;
import types.Rpc.CommitResult;
import types.Rpc.VerifyResult;

@SuppressWarnings("unchecked")
@PrepareForTest({AergoRPCServiceBlockingStub.class, Blockchain.Tx.class, VerifyResult.class,
    CommitResult.class})
public class SignEitherTemplateTest extends AbstractTestCase {

  @Test
  public void testSign() throws Exception {
    ResultOrErrorFuture<Signature> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(ResultOrError.class));
    SignAsyncOperation asyncOperationMock = mock(SignAsyncOperation.class);
    when(asyncOperationMock.sign(any(Transaction.class))).thenReturn(futureMock);

    final SignEitherTemplate signTemplate = new SignEitherTemplate(asyncOperationMock);

    final ResultOrError<Signature> signature = signTemplate.sign(new Transaction());
    assertNotNull(signature);
  }

  @Test
  public void testVerify() throws Exception {
    ResultOrErrorFuture<Boolean> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(ResultOrError.class));
    SignAsyncOperation asyncOperationMock = mock(SignAsyncOperation.class);
    when(asyncOperationMock.verify(any(Transaction.class))).thenReturn(futureMock);

    final SignEitherTemplate signTemplate = new SignEitherTemplate(asyncOperationMock);

    ResultOrError<Boolean> verifyResult = signTemplate.verify(new Transaction());
    assertNotNull(verifyResult);
  }

}