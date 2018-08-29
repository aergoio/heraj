/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.api.ContractAsyncOperation;
import hera.api.model.Hash;
import hera.api.model.Receipt;
import hera.api.tupleorerror.ResultOrError;
import hera.api.tupleorerror.ResultOrErrorFuture;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;

@SuppressWarnings("unchecked")
@PrepareForTest({AergoRPCServiceFutureStub.class})
public class ContractTemplateTest extends AbstractTestCase {

  @Test
  public void testGetReceipt() {
    ResultOrErrorFuture<Receipt> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(ResultOrError.class));
    ContractAsyncOperation asyncOperationMock = mock(ContractAsyncOperation.class);
    when(asyncOperationMock.getReceipt(any())).thenReturn(futureMock);

    final ContractTemplate contractTemplate = new ContractTemplate(asyncOperationMock);

    final ResultOrError<Receipt> receipt =
        contractTemplate.getReceipt(Hash.of(randomUUID().toString().getBytes()));
    assertNotNull(receipt);
  }

}
