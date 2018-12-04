
/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.CONTRACT_DEPLOY;
import static hera.TransportConstants.CONTRACT_EXECUTE;
import static hera.TransportConstants.CONTRACT_GETINTERFACE;
import static hera.TransportConstants.CONTRACT_GETRECEIPT;
import static hera.TransportConstants.CONTRACT_QUERY;
import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import hera.AbstractTestCase;
import hera.ContextProvider;
import hera.api.encode.Base58WithCheckSum;
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
public class ContractTemplateTest extends AbstractTestCase {

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

  protected ContractTemplate supplyContractTemplate(
      final ContractBaseTemplate contractBaseTemplate) {
    final ContractTemplate contractTemplate = new ContractTemplate();
    contractTemplate.contractBaseTemplate = contractBaseTemplate;
    contractTemplate.setContextProvider(ContextProvider.defaultProvider);
    return contractTemplate;
  }

  @Test
  public void testGetReceipt() {
    final ContractBaseTemplate base = mock(ContractBaseTemplate.class);
    ResultOrErrorFuture<ContractTxReceipt> future =
        ResultOrErrorFutureFactory.supply(() -> new ContractTxReceipt());
    when(base.getReceiptFunction()).thenReturn(h -> future);

    final ContractTemplate contractTemplate = supplyContractTemplate(base);

    final ContractTxReceipt receipt = contractTemplate
        .getReceipt(new ContractTxHash(of(randomUUID().toString().getBytes())));
    assertNotNull(receipt);
    assertEquals(CONTRACT_GETRECEIPT,
        ((WithIdentity) contractTemplate.getReceiptFunction()).getIdentity());
  }

  @Test
  public void testDeploy() throws Exception {
    final ContractBaseTemplate base = mock(ContractBaseTemplate.class);
    ResultOrErrorFuture<ContractTxHash> future =
        ResultOrErrorFutureFactory
            .supply(() -> new ContractTxHash(of(randomUUID().toString().getBytes())));
    when(base.getDeployFunction()).thenReturn((a, p, f) -> future);

    final ContractTemplate contractTemplate = supplyContractTemplate(base);

    Account account = mock(Account.class);
    Base58WithCheckSum encoded =
        () -> Base58Utils.encodeWithCheck(new byte[] {ContractDefinition.PAYLOAD_VERSION});
    final ContractTxHash deployTxHash =
        contractTemplate.deploy(account, ContractDefinition.of(encoded), fee);
    assertNotNull(deployTxHash);
    assertEquals(CONTRACT_DEPLOY,
        ((WithIdentity) contractTemplate.getDeployFunction()).getIdentity());
  }

  @Test
  public void testGetContractInterface() {
    final ContractBaseTemplate base = mock(ContractBaseTemplate.class);
    ResultOrErrorFuture<ContractInterface> future =
        ResultOrErrorFutureFactory.supply(() -> new ContractInterface());
    when(base.getContractInterfaceFunction()).thenReturn((a) -> future);

    final ContractTemplate contractTemplate = supplyContractTemplate(base);

    final ContractInterface contractInterface =
        contractTemplate.getContractInterface(contractAddress);
    assertNotNull(contractInterface);
    assertEquals(CONTRACT_GETINTERFACE,
        ((WithIdentity) contractTemplate.getContractInterfaceFunction()).getIdentity());
  }

  @Test
  public void testExecute() throws Exception {
    final ContractBaseTemplate base = mock(ContractBaseTemplate.class);
    ResultOrErrorFuture<ContractTxHash> future =
        ResultOrErrorFutureFactory
            .supply(() -> new ContractTxHash(of(randomUUID().toString().getBytes())));
    when(base.getExecuteFunction()).thenReturn((a, i, f) -> future);

    final ContractTemplate contractTemplate = supplyContractTemplate(base);

    Account account = mock(Account.class);
    final ContractTxHash executionTxHash = contractTemplate
        .execute(account, new ContractInvocation(contractAddress, new ContractFunction()), fee);
    assertNotNull(executionTxHash);
    assertEquals(CONTRACT_EXECUTE,
        ((WithIdentity) contractTemplate.getExecuteFunction()).getIdentity());
  }

  @Test
  public void testQuery() {
    final ContractBaseTemplate base = mock(ContractBaseTemplate.class);
    final ContractResult mockResult = mock(ContractResult.class);
    ResultOrErrorFuture<ContractResult> future =
        ResultOrErrorFutureFactory.supply(() -> mockResult);
    when(base.getQueryFunction()).thenReturn((i) -> future);

    final ContractTemplate contractTemplate = supplyContractTemplate(base);

    final ContractResult contractResult = contractTemplate
        .query(new ContractInvocation(contractAddress, new ContractFunction()));

    assertNotNull(contractResult);
    assertEquals(CONTRACT_QUERY,
        ((WithIdentity) contractTemplate.getQueryFunction()).getIdentity());
  }

}
