/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.api.model.AccountAddress;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractFunction;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.model.ServerManagedAccount;
import hera.api.tupleorerror.ResultOrError;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceStub;

@SuppressWarnings("unchecked")
@PrepareForTest({AergoRPCServiceStub.class})
public class ContractTemplateTest extends AbstractTestCase {

  protected final AccountAddress accountAddress =
      new AccountAddress(of(new byte[] {AccountAddress.VERSION}));

  protected final ContractAddress contractAddress =
      new ContractAddress(of(new byte[] {AccountAddress.VERSION}));

  @Test
  public void testGetReceipt() {
    ResultOrError<ContractTxReceipt> eitherMock = mock(ResultOrError.class);
    when(eitherMock.getResult()).thenReturn(mock(ContractTxReceipt.class));
    ContractEitherTemplate eitherOperationMock = mock(ContractEitherTemplate.class);
    when(eitherOperationMock.getReceipt(any())).thenReturn(eitherMock);

    final ContractTemplate contractTemplate = new ContractTemplate();
    contractTemplate.contractEitherOperation = eitherOperationMock;

    final ContractTxReceipt receipt =
        contractTemplate.getReceipt(new ContractTxHash(of(randomUUID().toString().getBytes())));
    assertNotNull(receipt);
  }

  @Test
  public void testDeploy() {
    ResultOrError<ContractTxHash> eitherMock = mock(ResultOrError.class);
    when(eitherMock.getResult()).thenReturn(mock(ContractTxHash.class));
    ContractEitherTemplate eitherOperationMock = mock(ContractEitherTemplate.class);
    when(eitherOperationMock.deploy(any(), any())).thenReturn(eitherMock);

    final ContractTemplate contractTemplate = new ContractTemplate();
    contractTemplate.contractEitherOperation = eitherOperationMock;

    final ContractTxHash deployTxHash =
        contractTemplate.deploy(ServerManagedAccount.of(accountAddress),
            ContractDefinition.of(() -> randomUUID().toString()));
    assertNotNull(deployTxHash);
  }

  @Test
  public void testGetContractInterface() {
    ResultOrError<ContractInterface> eitherMock = mock(ResultOrError.class);
    when(eitherMock.getResult()).thenReturn(mock(ContractInterface.class));
    ContractEitherTemplate eitherOperationMock = mock(ContractEitherTemplate.class);
    when(eitherOperationMock.getContractInterface(any())).thenReturn(eitherMock);

    final ContractTemplate contractTemplate = new ContractTemplate();
    contractTemplate.contractEitherOperation = eitherOperationMock;

    final ContractInterface contractInterface =
        contractTemplate.getContractInterface(contractAddress);
    assertNotNull(contractInterface);
  }

  @Test
  public void testExecute() {
    ResultOrError<ContractTxHash> eitherMock = mock(ResultOrError.class);
    when(eitherMock.getResult()).thenReturn(mock(ContractTxHash.class));
    ContractEitherTemplate eitherOperationMock = mock(ContractEitherTemplate.class);
    when(eitherOperationMock.execute(any(), any())).thenReturn(eitherMock);

    final ContractTemplate contractTemplate = new ContractTemplate();
    contractTemplate.contractEitherOperation = eitherOperationMock;

    final ContractTxHash executionTxHash =
        contractTemplate.execute(ServerManagedAccount.of(accountAddress),
            new ContractInvocation(contractAddress, new ContractFunction()));
    assertNotNull(executionTxHash);

  }

  @Test
  public void query() {
    ResultOrError<ContractResult> eitherMock = mock(ResultOrError.class);
    when(eitherMock.getResult()).thenReturn(mock(ContractResult.class));
    ContractEitherTemplate eitherOperationMock = mock(ContractEitherTemplate.class);
    when(eitherOperationMock.query(any())).thenReturn(eitherMock);

    final ContractTemplate contractTemplate = new ContractTemplate();
    contractTemplate.contractEitherOperation = eitherOperationMock;

    final ContractResult queryResult =
        contractTemplate.query(new ContractInvocation(contractAddress, new ContractFunction()));
    assertNotNull(queryResult);
  }

}
