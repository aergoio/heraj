/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.api.ContractAsyncOperation;
import hera.api.model.Abi;
import hera.api.model.AbiSet;
import hera.api.model.AccountAddress;
import hera.api.model.ContractTxHash;
import hera.api.model.Receipt;
import hera.api.tupleorerror.ResultOrError;
import hera.api.tupleorerror.ResultOrErrorFuture;
import java.io.InputStream;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;

@SuppressWarnings("unchecked")
@PrepareForTest({AergoRPCServiceFutureStub.class})
public class ContractTemplateTest extends AbstractTestCase {

  protected static final AccountAddress EXECUTOR_ADDRESS =
      AccountAddress.of(randomUUID().toString().getBytes());

  protected static final AccountAddress CONTRACT_ADDRESS =
      AccountAddress.of(randomUUID().toString().getBytes());

  @Test
  public void testGetReceipt() {
    ResultOrErrorFuture<Receipt> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(ResultOrError.class));
    ContractAsyncOperation asyncOperationMock = mock(ContractAsyncOperation.class);
    when(asyncOperationMock.getReceipt(any())).thenReturn(futureMock);

    final ContractTemplate contractTemplate = new ContractTemplate(asyncOperationMock);

    final ResultOrError<Receipt> receipt =
        contractTemplate.getReceipt(ContractTxHash.of(randomUUID().toString().getBytes()));
    assertNotNull(receipt);
  }

  @Test
  public void testDeploy() {
    ResultOrErrorFuture<ContractTxHash> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(ResultOrError.class));
    ContractAsyncOperation asyncOperationMock = mock(ContractAsyncOperation.class);
    when(asyncOperationMock.deploy(any(), any())).thenReturn(futureMock);

    final ContractTemplate contractTemplate = new ContractTemplate(asyncOperationMock);

    final ResultOrError<ContractTxHash> deployTxHash =
        contractTemplate.deploy(EXECUTOR_ADDRESS, () -> mock(InputStream.class));
    assertNotNull(deployTxHash);
  }

  @Test
  public void testGetAbiSet() {
    ResultOrErrorFuture<AbiSet> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(ResultOrError.class));
    ContractAsyncOperation asyncOperationMock = mock(ContractAsyncOperation.class);
    when(asyncOperationMock.getAbiSet(any())).thenReturn(futureMock);

    final ContractTemplate contractTemplate = new ContractTemplate(asyncOperationMock);

    final ResultOrError<AbiSet> abiSet = contractTemplate.getAbiSet(CONTRACT_ADDRESS);
    assertNotNull(abiSet);
  }

  @Test
  public void testGetAbi() {
    ResultOrErrorFuture<Abi> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(ResultOrError.class));
    ContractAsyncOperation asyncOperationMock = mock(ContractAsyncOperation.class);
    when(asyncOperationMock.getAbi(any(), anyString())).thenReturn(futureMock);

    final ContractTemplate contractTemplate = new ContractTemplate(asyncOperationMock);

    final ResultOrError<Abi> abi =
        contractTemplate.getAbi(CONTRACT_ADDRESS, randomUUID().toString());
    assertNotNull(abi);
  }

  @Test
  public void testExecute() {
    ResultOrErrorFuture<ContractTxHash> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(ResultOrError.class));
    ContractAsyncOperation asyncOperationMock = mock(ContractAsyncOperation.class);
    when(asyncOperationMock.execute(any(), any(), any(), any())).thenReturn(futureMock);

    final ContractTemplate contractTemplate = new ContractTemplate(asyncOperationMock);

    final ResultOrError<ContractTxHash> executionTxHash =
        contractTemplate.execute(EXECUTOR_ADDRESS, CONTRACT_ADDRESS, new Abi(), randomUUID());
    assertNotNull(executionTxHash);

  }

  @Test
  public void query() {
    ResultOrErrorFuture<Object> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(ResultOrError.class));
    ContractAsyncOperation asyncOperationMock = mock(ContractAsyncOperation.class);
    when(asyncOperationMock.query(any(), any(), any())).thenReturn(futureMock);

    final ContractTemplate contractTemplate = new ContractTemplate(asyncOperationMock);

    final ResultOrError<Object> queryResult =
        contractTemplate.query(CONTRACT_ADDRESS, new Abi(), randomUUID());
    assertNotNull(queryResult);
  }

}
