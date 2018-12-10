/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.CONTRACT_DEPLOY_ASYNC;
import static hera.TransportConstants.CONTRACT_EXECUTE_ASYNC;
import static hera.TransportConstants.CONTRACT_GETINTERFACE_ASYNC;
import static hera.TransportConstants.CONTRACT_GETRECEIPT_ASYNC;
import static hera.TransportConstants.CONTRACT_QUERY_ASYNC;
import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import hera.AbstractTestCase;
import hera.ContextProvider;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractFunction;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.model.Fee;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.api.tupleorerror.WithIdentity;
import hera.key.AergoKeyGenerator;
import hera.util.Base58Utils;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

@PrepareForTest({ContractBaseTemplate.class})
public class ContractAsyncTemplateTest extends AbstractTestCase {

  protected final AccountAddress accountAddress =
      new AccountAddress(of(new byte[] {AccountAddress.VERSION}));

  protected final ContractAddress contractAddress =
      new ContractAddress(of(new byte[] {AccountAddress.VERSION}));

  protected final Fee fee = Fee.getDefaultFee();

  protected final AergoKeyGenerator generator = new AergoKeyGenerator();

  @Override
  public void setUp() {
    super.setUp();
  }

  protected ContractAsyncTemplate supplyContractAsyncTemplate(
      final ContractBaseTemplate contractBaseTemplate) {
    final ContractAsyncTemplate contractAsyncTemplate = new ContractAsyncTemplate();
    contractAsyncTemplate.contractBaseTemplate = contractBaseTemplate;
    contractAsyncTemplate.setContextProvider(ContextProvider.defaultProvider);
    return contractAsyncTemplate;
  }

  @Test
  public void testGetReceipt() {
    final ContractBaseTemplate base = mock(ContractBaseTemplate.class);
    ResultOrErrorFuture<ContractTxReceipt> future =
        ResultOrErrorFutureFactory.supply(() -> new ContractTxReceipt());
    when(base.getReceiptFunction()).thenReturn(h -> future);

    final ContractAsyncTemplate contractAsyncTemplate = supplyContractAsyncTemplate(base);

    final ResultOrErrorFuture<ContractTxReceipt> receipt = contractAsyncTemplate
        .getReceipt(new ContractTxHash(of(randomUUID().toString().getBytes())));
    assertTrue(receipt.get().hasResult());
    assertEquals(CONTRACT_GETRECEIPT_ASYNC,
        ((WithIdentity) contractAsyncTemplate.getReceiptFunction()).getIdentity());
  }

  @Test
  public void testDeploy() throws Exception {
    final ContractBaseTemplate base = mock(ContractBaseTemplate.class);
    ResultOrErrorFuture<ContractTxHash> future =
        ResultOrErrorFutureFactory
            .supply(() -> new ContractTxHash(of(randomUUID().toString().getBytes())));
    when(base.getDeployFunction()).thenReturn((a, p, n, f) -> future);

    final ContractAsyncTemplate contractAsyncTemplate = supplyContractAsyncTemplate(base);

    final Account account = mock(Account.class);
    String encoded =
        Base58Utils.encodeWithCheck(new byte[] {ContractDefinition.PAYLOAD_VERSION});
    final ResultOrErrorFuture<ContractTxHash> deployTxHash =
        contractAsyncTemplate.deploy(account, ContractDefinition.of(encoded), 0, fee);
    assertTrue(deployTxHash.get().hasResult());
    assertEquals(CONTRACT_DEPLOY_ASYNC,
        ((WithIdentity) contractAsyncTemplate.getDeployFunction()).getIdentity());
  }

  @Test
  public void testGetContractInterface() {
    final ContractBaseTemplate base = mock(ContractBaseTemplate.class);
    final ContractInterface mockContractInterface = mock(ContractInterface.class);
    ResultOrErrorFuture<ContractInterface> future =
        ResultOrErrorFutureFactory.supply(() -> mockContractInterface);
    when(base.getContractInterfaceFunction()).thenReturn((a) -> future);

    final ContractAsyncTemplate contractAsyncTemplate = supplyContractAsyncTemplate(base);

    final ResultOrErrorFuture<ContractInterface> contractInterface =
        contractAsyncTemplate.getContractInterface(contractAddress);
    assertTrue(contractInterface.get().hasResult());
    assertEquals(CONTRACT_GETINTERFACE_ASYNC,
        ((WithIdentity) contractAsyncTemplate.getContractInterfaceFunction()).getIdentity());
  }

  @Test
  public void testExecute() throws Exception {
    final ContractBaseTemplate base = mock(ContractBaseTemplate.class);
    ResultOrErrorFuture<ContractTxHash> future =
        ResultOrErrorFutureFactory
            .supply(() -> new ContractTxHash(of(randomUUID().toString().getBytes())));
    when(base.getExecuteFunction()).thenReturn((a, i, n, f) -> future);

    final ContractAsyncTemplate contractAsyncTemplate = supplyContractAsyncTemplate(base);

    final Account account = mock(Account.class);
    final ContractFunction contractFunction = new ContractFunction(randomUUID().toString());
    final ResultOrErrorFuture<ContractTxHash> executionTxHash = contractAsyncTemplate
        .execute(account, new ContractInvocation(contractAddress, contractFunction), 0, fee);
    assertTrue(executionTxHash.get().hasResult());
    assertEquals(CONTRACT_EXECUTE_ASYNC,
        ((WithIdentity) contractAsyncTemplate.getExecuteFunction()).getIdentity());
  }

  @Test
  public void testQuery() {
    final ContractBaseTemplate base = mock(ContractBaseTemplate.class);
    final ContractResult mockResult = mock(ContractResult.class);
    ResultOrErrorFuture<ContractResult> future =
        ResultOrErrorFutureFactory.supply(() -> mockResult);
    when(base.getQueryFunction()).thenReturn((i) -> future);

    final ContractAsyncTemplate contractAsyncTemplate = supplyContractAsyncTemplate(base);

    final ContractFunction contractFunction = new ContractFunction(randomUUID().toString());
    final ResultOrErrorFuture<ContractResult> contractResult = contractAsyncTemplate
        .query(new ContractInvocation(contractAddress, contractFunction));

    assertTrue(contractResult.get().hasResult());
    assertEquals(CONTRACT_QUERY_ASYNC,
        ((WithIdentity) contractAsyncTemplate.getQueryFunction()).getIdentity());
  }

}
