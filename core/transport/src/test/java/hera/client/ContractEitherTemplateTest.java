/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.api.ContractAsyncOperation;
import hera.api.model.AccountAddress;
import hera.api.model.ContractAddress;
import hera.api.model.ContractFunction;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.tupleorerror.ResultOrError;
import hera.api.tupleorerror.ResultOrErrorFuture;
import java.util.concurrent.TimeoutException;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;

@SuppressWarnings("unchecked")
@PrepareForTest({AergoRPCServiceFutureStub.class})
public class ContractEitherTemplateTest extends AbstractTestCase {

  protected static final AccountAddress EXECUTOR_ADDRESS =
      new AccountAddress(of(new byte[] {AccountAddress.VERSION}));

  protected static final ContractAddress CONTRACT_ADDRESS =
      new ContractAddress(of(new byte[] {AccountAddress.VERSION}));

  @Test
  public void testGetReceipt() {
    ResultOrErrorFuture<ContractTxReceipt> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(ResultOrError.class));
    ContractAsyncOperation asyncOperationMock = mock(ContractAsyncOperation.class);
    when(asyncOperationMock.getReceipt(any())).thenReturn(futureMock);

    final ContractEitherTemplate contractTemplate = new ContractEitherTemplate(asyncOperationMock);

    final ResultOrError<ContractTxReceipt> receipt =
        contractTemplate.getReceipt(new ContractTxHash(of(randomUUID().toString().getBytes())));
    assertNotNull(receipt);
  }

  @Test
  public void testGetReceiptWithTimeout() {
    ResultOrErrorFuture<ContractTxReceipt> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenThrow(TimeoutException.class);
    ContractAsyncOperation asyncOperationMock = mock(ContractAsyncOperation.class);
    when(asyncOperationMock.getReceipt(any())).thenReturn(futureMock);

    final ContractEitherTemplate contractTemplate = new ContractEitherTemplate(asyncOperationMock);

    final ResultOrError<ContractTxReceipt> receipt =
        contractTemplate.getReceipt(new ContractTxHash(of(randomUUID().toString().getBytes())));
    assertTrue(receipt.hasError());
  }

  @Test
  public void testDeploy() {
    ResultOrErrorFuture<ContractTxHash> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(ResultOrError.class));
    ContractAsyncOperation asyncOperationMock = mock(ContractAsyncOperation.class);
    when(asyncOperationMock.deploy(any(), any(AccountAddress.class), anyLong(), any()))
        .thenReturn(futureMock);

    final ContractEitherTemplate contractTemplate = new ContractEitherTemplate(asyncOperationMock);

    final ResultOrError<ContractTxHash> deployTxHash = contractTemplate.deploy(null,
        EXECUTOR_ADDRESS, randomUUID().hashCode(), () -> randomUUID().toString());
    assertNotNull(deployTxHash);
  }

  @Test
  public void testDeployWithTimeout() {
    ResultOrErrorFuture<ContractTxHash> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenThrow(TimeoutException.class);
    ContractAsyncOperation asyncOperationMock = mock(ContractAsyncOperation.class);
    when(asyncOperationMock.deploy(any(), any(AccountAddress.class), anyLong(), any()))
        .thenReturn(futureMock);

    final ContractEitherTemplate contractTemplate = new ContractEitherTemplate(asyncOperationMock);

    final ResultOrError<ContractTxHash> deployTxHash = contractTemplate.deploy(null,
        EXECUTOR_ADDRESS, randomUUID().hashCode(), () -> randomUUID().toString());
    assertTrue(deployTxHash.hasError());
  }

  @Test
  public void testGetContractInterface() {
    ResultOrErrorFuture<ContractInterface> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(ResultOrError.class));
    ContractAsyncOperation asyncOperationMock = mock(ContractAsyncOperation.class);
    when(asyncOperationMock.getContractInterface(any())).thenReturn(futureMock);

    final ContractEitherTemplate contractTemplate = new ContractEitherTemplate(asyncOperationMock);

    final ResultOrError<ContractInterface> contractInterface =
        contractTemplate.getContractInterface(CONTRACT_ADDRESS);
    assertNotNull(contractInterface);
  }

  @Test
  public void testGetContractInterfaceWithTimeout() {
    ResultOrErrorFuture<ContractInterface> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenThrow(TimeoutException.class);
    ContractAsyncOperation asyncOperationMock = mock(ContractAsyncOperation.class);
    when(asyncOperationMock.getContractInterface(any())).thenReturn(futureMock);

    final ContractEitherTemplate contractTemplate = new ContractEitherTemplate(asyncOperationMock);

    final ResultOrError<ContractInterface> contractInterface =
        contractTemplate.getContractInterface(CONTRACT_ADDRESS);
    assertTrue(contractInterface.hasError());
  }

  @Test
  public void testExecute() {
    ResultOrErrorFuture<ContractTxHash> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(ResultOrError.class));
    ContractAsyncOperation asyncOperationMock = mock(ContractAsyncOperation.class);
    when(asyncOperationMock.execute(any(), any(AccountAddress.class), anyLong(), any()))
        .thenReturn(futureMock);

    final ContractEitherTemplate contractTemplate = new ContractEitherTemplate(asyncOperationMock);

    final ResultOrError<ContractTxHash> executionTxHash =
        contractTemplate.execute(null, EXECUTOR_ADDRESS, randomUUID().hashCode(),
            new ContractInvocation(CONTRACT_ADDRESS, new ContractFunction()));
    assertNotNull(executionTxHash);
  }

  @Test
  public void testExecuteWithTimeout() {
    ResultOrErrorFuture<ContractTxHash> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenThrow(TimeoutException.class);
    ContractAsyncOperation asyncOperationMock = mock(ContractAsyncOperation.class);
    when(asyncOperationMock.execute(any(), any(AccountAddress.class), anyLong(), any()))
        .thenReturn(futureMock);

    final ContractEitherTemplate contractTemplate = new ContractEitherTemplate(asyncOperationMock);

    final ResultOrError<ContractTxHash> executionTxHash =
        contractTemplate.execute(null, EXECUTOR_ADDRESS, randomUUID().hashCode(),
            new ContractInvocation(CONTRACT_ADDRESS, new ContractFunction()));
    assertTrue(executionTxHash.hasError());
  }

  @Test
  public void query() {
    ResultOrErrorFuture<ContractResult> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(ResultOrError.class));
    ContractAsyncOperation asyncOperationMock = mock(ContractAsyncOperation.class);
    when(asyncOperationMock.query(any())).thenReturn(futureMock);

    final ContractEitherTemplate contractTemplate = new ContractEitherTemplate(asyncOperationMock);

    final ResultOrError<ContractResult> queryResult =
        contractTemplate.query(new ContractInvocation(CONTRACT_ADDRESS, new ContractFunction()));
    assertNotNull(queryResult);
  }

  @Test
  public void queryWithTimeout() {
    ResultOrErrorFuture<ContractResult> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenThrow(TimeoutException.class);
    ContractAsyncOperation asyncOperationMock = mock(ContractAsyncOperation.class);
    when(asyncOperationMock.query(any())).thenReturn(futureMock);

    final ContractEitherTemplate contractTemplate = new ContractEitherTemplate(asyncOperationMock);

    final ResultOrError<ContractResult> queryResult =
        contractTemplate.query(new ContractInvocation(CONTRACT_ADDRESS, new ContractFunction()));
    assertTrue(queryResult.hasError());
  }

}
