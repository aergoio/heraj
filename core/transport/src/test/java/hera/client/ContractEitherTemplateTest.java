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
import hera.Context;
import hera.api.model.AccountAddress;
import hera.api.model.ContractAddress;
import hera.api.model.ContractFunction;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.model.ServerManagedAccount;
import hera.api.tupleorerror.ResultOrError;
import hera.api.tupleorerror.ResultOrErrorFuture;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;

@SuppressWarnings("unchecked")
@PrepareForTest({AergoRPCServiceFutureStub.class})
public class ContractEitherTemplateTest extends AbstractTestCase {

  protected final Context context = AergoClientBuilder.getDefaultContext();

  protected final AccountAddress accountAddress =
      new AccountAddress(of(new byte[] {AccountAddress.VERSION}));

  protected final ContractAddress contractAddress =
      new ContractAddress(of(new byte[] {AccountAddress.VERSION}));

  @Test
  public void testGetReceipt() {
    ResultOrErrorFuture<ContractTxReceipt> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(ResultOrError.class));
    ContractAsyncTemplate asyncOperationMock = mock(ContractAsyncTemplate.class);
    when(asyncOperationMock.getReceipt(any())).thenReturn(futureMock);

    final ContractEitherTemplate contractTemplate = new ContractEitherTemplate();
    contractTemplate.setContext(context);
    contractTemplate.contractAsyncOperation = asyncOperationMock;

    final ResultOrError<ContractTxReceipt> receipt =
        contractTemplate.getReceipt(new ContractTxHash(of(randomUUID().toString().getBytes())));
    assertNotNull(receipt);
  }

  @Test
  public void testDeploy() {
    ResultOrErrorFuture<ContractTxHash> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(ResultOrError.class));
    ContractAsyncTemplate asyncOperationMock = mock(ContractAsyncTemplate.class);
    when(asyncOperationMock.deploy(any(), anyLong(), any())).thenReturn(futureMock);

    final ContractEitherTemplate contractTemplate = new ContractEitherTemplate();
    contractTemplate.setContext(context);
    contractTemplate.contractAsyncOperation = asyncOperationMock;

    final ResultOrError<ContractTxHash> deployTxHash =
        contractTemplate.deploy(ServerManagedAccount.of(accountAddress), randomUUID().hashCode(),
            () -> randomUUID().toString());
    assertNotNull(deployTxHash);
  }

  @Test
  public void testGetContractInterface() {
    ResultOrErrorFuture<ContractInterface> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(ResultOrError.class));
    ContractAsyncTemplate asyncOperationMock = mock(ContractAsyncTemplate.class);
    when(asyncOperationMock.getContractInterface(any())).thenReturn(futureMock);

    final ContractEitherTemplate contractTemplate = new ContractEitherTemplate();
    contractTemplate.setContext(context);
    contractTemplate.contractAsyncOperation = asyncOperationMock;

    final ResultOrError<ContractInterface> contractInterface =
        contractTemplate.getContractInterface(contractAddress);
    assertNotNull(contractInterface);
  }

  @Test
  public void testExecute() {
    ResultOrErrorFuture<ContractTxHash> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(ResultOrError.class));
    ContractAsyncTemplate asyncOperationMock = mock(ContractAsyncTemplate.class);
    when(asyncOperationMock.execute(any(), anyLong(), any())).thenReturn(futureMock);

    final ContractEitherTemplate contractTemplate = new ContractEitherTemplate();
    contractTemplate.setContext(context);
    contractTemplate.contractAsyncOperation = asyncOperationMock;

    final ResultOrError<ContractTxHash> executionTxHash =
        contractTemplate.execute(ServerManagedAccount.of(accountAddress), randomUUID().hashCode(),
            new ContractInvocation(contractAddress, new ContractFunction()));
    assertNotNull(executionTxHash);
  }

  @Test
  public void query() {
    ResultOrErrorFuture<ContractResult> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(ResultOrError.class));
    ContractAsyncTemplate asyncOperationMock = mock(ContractAsyncTemplate.class);
    when(asyncOperationMock.query(any())).thenReturn(futureMock);

    final ContractEitherTemplate contractTemplate = new ContractEitherTemplate();
    contractTemplate.setContext(context);
    contractTemplate.contractAsyncOperation = asyncOperationMock;

    final ResultOrError<ContractResult> queryResult =
        contractTemplate.query(new ContractInvocation(contractAddress, new ContractFunction()));
    assertNotNull(queryResult);
  }

}
