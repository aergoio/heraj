/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.api.ContractAsyncOperation;
import hera.api.model.AccountAddress;
import hera.api.model.ContractAddress;
import hera.api.model.ContractFunction;
import hera.api.model.ContractInferface;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.tupleorerror.ResultOrError;
import hera.api.tupleorerror.ResultOrErrorFuture;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;

@SuppressWarnings("unchecked")
@PrepareForTest({AergoRPCServiceFutureStub.class})
public class ContractEitherTemplateTest extends AbstractTestCase {

  protected static final AccountAddress EXECUTOR_ADDRESS =
      new AccountAddress(of(new byte[] {AccountAddress.ADDRESS_VERSION}));

  protected static final ContractAddress CONTRACT_ADDRESS =
      new ContractAddress(of(new byte[] {AccountAddress.ADDRESS_VERSION}));

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
  public void testDeploy() {
    ResultOrErrorFuture<ContractTxHash> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(ResultOrError.class));
    ContractAsyncOperation asyncOperationMock = mock(ContractAsyncOperation.class);
    when(asyncOperationMock.deploy(any(AccountAddress.class), any())).thenReturn(futureMock);

    final ContractEitherTemplate contractTemplate = new ContractEitherTemplate(asyncOperationMock);

    final ResultOrError<ContractTxHash> deployTxHash =
        contractTemplate.deploy(EXECUTOR_ADDRESS, () -> new byte[] {});
    assertNotNull(deployTxHash);
  }

  @Test
  public void testGetContractInterface() {
    ResultOrErrorFuture<ContractInferface> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(ResultOrError.class));
    ContractAsyncOperation asyncOperationMock = mock(ContractAsyncOperation.class);
    when(asyncOperationMock.getContractInterface(any())).thenReturn(futureMock);

    final ContractEitherTemplate contractTemplate = new ContractEitherTemplate(asyncOperationMock);

    final ResultOrError<ContractInferface> contractInterface =
        contractTemplate.getContractInterface(CONTRACT_ADDRESS);
    assertNotNull(contractInterface);
  }

  @Test
  public void testExecute() {
    ResultOrErrorFuture<ContractTxHash> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(ResultOrError.class));
    ContractAsyncOperation asyncOperationMock = mock(ContractAsyncOperation.class);
    when(asyncOperationMock.execute(any(AccountAddress.class), any(), any(), any()))
        .thenReturn(futureMock);

    final ContractEitherTemplate contractTemplate = new ContractEitherTemplate(asyncOperationMock);

    final ResultOrError<ContractTxHash> executionTxHash = contractTemplate.execute(EXECUTOR_ADDRESS,
        CONTRACT_ADDRESS, new ContractFunction(), randomUUID());
    assertNotNull(executionTxHash);

  }

  @Test
  public void query() {
    ResultOrErrorFuture<ContractResult> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(ResultOrError.class));
    ContractAsyncOperation asyncOperationMock = mock(ContractAsyncOperation.class);
    when(asyncOperationMock.query(any(), any(), any())).thenReturn(futureMock);

    final ContractEitherTemplate contractTemplate = new ContractEitherTemplate(asyncOperationMock);

    final ResultOrError<ContractResult> queryResult =
        contractTemplate.query(CONTRACT_ADDRESS, new ContractFunction(), randomUUID());
    assertNotNull(queryResult);
  }

}
