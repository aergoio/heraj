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
import hera.api.ContractEitherOperation;
import hera.api.model.AccountAddress;
import hera.api.model.ContractAddress;
import hera.api.model.ContractFunction;
import hera.api.model.ContractInterface;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.tupleorerror.ResultOrError;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceStub;

@SuppressWarnings("unchecked")
@PrepareForTest({AergoRPCServiceStub.class})
public class ContractTemplateTest extends AbstractTestCase {

  protected static final AccountAddress EXECUTOR_ADDRESS =
      new AccountAddress(of(new byte[] {AccountAddress.VERSION}));

  protected static final ContractAddress CONTRACT_ADDRESS =
      new ContractAddress(of(new byte[] {AccountAddress.VERSION}));

  @Test
  public void testGetReceipt() {
    ResultOrError<ContractTxReceipt> eitherMock = mock(ResultOrError.class);
    when(eitherMock.getResult()).thenReturn(mock(ContractTxReceipt.class));
    ContractEitherOperation eitherOperationMock = mock(ContractEitherOperation.class);
    when(eitherOperationMock.getReceipt(any())).thenReturn(eitherMock);

    final ContractTemplate contractTemplate = new ContractTemplate(eitherOperationMock);

    final ContractTxReceipt receipt =
        contractTemplate.getReceipt(new ContractTxHash(of(randomUUID().toString().getBytes())));
    assertNotNull(receipt);
  }

  @Test
  public void testDeploy() {
    ResultOrError<ContractTxHash> eitherMock = mock(ResultOrError.class);
    when(eitherMock.getResult()).thenReturn(mock(ContractTxHash.class));
    ContractEitherOperation eitherOperationMock = mock(ContractEitherOperation.class);
    when(eitherOperationMock.deploy(any(AccountAddress.class), any())).thenReturn(eitherMock);

    final ContractTemplate contractTemplate = new ContractTemplate(eitherOperationMock);

    final ContractTxHash deployTxHash =
        contractTemplate.deploy(EXECUTOR_ADDRESS, () -> new byte[] {});
    assertNotNull(deployTxHash);
  }

  @Test
  public void testGetContractInterface() {
    ResultOrError<ContractInterface> eitherMock = mock(ResultOrError.class);
    when(eitherMock.getResult()).thenReturn(mock(ContractInterface.class));
    ContractEitherOperation eitherOperationMock = mock(ContractEitherOperation.class);
    when(eitherOperationMock.getContractInterface(any())).thenReturn(eitherMock);

    final ContractTemplate contractTemplate = new ContractTemplate(eitherOperationMock);

    final ContractInterface contractInterface =
        contractTemplate.getContractInterface(CONTRACT_ADDRESS);
    assertNotNull(contractInterface);
  }

  @Test
  public void testExecute() {
    ResultOrError<ContractTxHash> eitherMock = mock(ResultOrError.class);
    when(eitherMock.getResult()).thenReturn(mock(ContractTxHash.class));
    ContractEitherOperation eitherOperationMock = mock(ContractEitherOperation.class);
    when(eitherOperationMock.execute(any(AccountAddress.class), any(), any(), any()))
        .thenReturn(eitherMock);

    final ContractTemplate contractTemplate = new ContractTemplate(eitherOperationMock);

    final ContractTxHash executionTxHash = contractTemplate.execute(EXECUTOR_ADDRESS,
        CONTRACT_ADDRESS, new ContractFunction(), randomUUID());
    assertNotNull(executionTxHash);

  }

  @Test
  public void query() {
    ResultOrError<ContractResult> eitherMock = mock(ResultOrError.class);
    when(eitherMock.getResult()).thenReturn(mock(ContractResult.class));
    ContractEitherOperation eitherOperationMock = mock(ContractEitherOperation.class);
    when(eitherOperationMock.query(any(), any(), any())).thenReturn(eitherMock);

    final ContractTemplate contractTemplate = new ContractTemplate(eitherOperationMock);

    final ContractResult queryResult =
        contractTemplate.query(CONTRACT_ADDRESS, new ContractFunction(), randomUUID());
    assertNotNull(queryResult);
  }

}
