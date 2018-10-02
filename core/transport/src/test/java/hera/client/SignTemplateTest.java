/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import hera.AbstractTestCase;
import hera.api.SignEitherOperation;
import hera.api.model.Signature;
import hera.api.model.Transaction;
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
public class SignTemplateTest extends AbstractTestCase {

  @Test
  public void testSign() throws Exception {
    ResultOrError<Signature> eitherMock = mock(ResultOrError.class);
    when(eitherMock.getResult()).thenReturn(mock(Signature.class));
    SignEitherOperation eitherOperationMock = mock(SignEitherOperation.class);
    when(eitherOperationMock.sign(any(Transaction.class))).thenReturn(eitherMock);

    final SignTemplate signTemplate = new SignTemplate(eitherOperationMock);

    final Signature signature = signTemplate.sign(new Transaction());
    assertNotNull(signature);
  }

  @Test
  public void testVerify() throws Exception {
    ResultOrError<Boolean> eitherMock = mock(ResultOrError.class);
    when(eitherMock.getResult()).thenReturn(true);
    SignEitherOperation eitherOperationMock = mock(SignEitherOperation.class);
    when(eitherOperationMock.verify(any(Transaction.class))).thenReturn(eitherMock);

    final SignTemplate signTemplate = new SignTemplate(eitherOperationMock);

    final boolean verifyResult = signTemplate.verify(new Transaction());
    assertTrue(verifyResult);
  }

}