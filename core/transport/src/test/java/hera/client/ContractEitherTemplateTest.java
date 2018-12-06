/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.CONTRACT_DEPLOY_EITHER;
import static hera.TransportConstants.CONTRACT_EXECUTE_EITHER;
import static hera.TransportConstants.CONTRACT_GETINTERFACE_EITHER;
import static hera.TransportConstants.CONTRACT_GETRECEIPT_EITHER;
import static hera.TransportConstants.CONTRACT_QUERY_EITHER;
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
import hera.api.tupleorerror.ResultOrError;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.api.tupleorerror.WithIdentity;
import hera.key.AergoKeyGenerator;
import hera.util.Base58Utils;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

@PrepareForTest({ContractBaseTemplate.class})
public class ContractEitherTemplateTest extends AbstractTestCase {

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

  protected ContractEitherTemplate supplyContractEitherTemplate(
      final ContractBaseTemplate contractBaseTemplate) {
    final ContractEitherTemplate contractEitherTemplate = new ContractEitherTemplate();
    contractEitherTemplate.contractBaseTemplate = contractBaseTemplate;
    contractEitherTemplate.setContextProvider(ContextProvider.defaultProvider);
    return contractEitherTemplate;
  }

  @Test
  public void testGetReceipt() {
    final ContractBaseTemplate base = mock(ContractBaseTemplate.class);
    ResultOrErrorFuture<ContractTxReceipt> future =
        ResultOrErrorFutureFactory.supply(() -> new ContractTxReceipt());
    when(base.getReceiptFunction()).thenReturn(h -> future);

    final ContractEitherTemplate contractEitherTemplate = supplyContractEitherTemplate(base);

    final ResultOrError<ContractTxReceipt> receipt = contractEitherTemplate
        .getReceipt(new ContractTxHash(of(randomUUID().toString().getBytes())));
    assertTrue(receipt.hasResult());
    assertEquals(CONTRACT_GETRECEIPT_EITHER,
        ((WithIdentity) contractEitherTemplate.getReceiptFunction()).getIdentity());
  }

  @Test
  public void testDeploy() throws Exception {
    final ContractBaseTemplate base = mock(ContractBaseTemplate.class);
    ResultOrErrorFuture<ContractTxHash> future =
        ResultOrErrorFutureFactory
            .supply(() -> new ContractTxHash(of(randomUUID().toString().getBytes())));
    when(base.getDeployFunction()).thenReturn((a, p, f) -> future);

    final ContractEitherTemplate contractEitherTemplate = supplyContractEitherTemplate(base);

    final Account account = mock(Account.class);
    String encoded = Base58Utils.encodeWithCheck(new byte[] {ContractDefinition.PAYLOAD_VERSION});
    final ResultOrError<ContractTxHash> deployTxHash =
        contractEitherTemplate.deploy(account, ContractDefinition.of(encoded), fee);
    assertTrue(deployTxHash.hasResult());
    assertEquals(CONTRACT_DEPLOY_EITHER,
        ((WithIdentity) contractEitherTemplate.getDeployFunction()).getIdentity());
  }

  @Test
  public void testGetContractInterface() {
    final ContractBaseTemplate base = mock(ContractBaseTemplate.class);
    final ContractInterface mockContractInterface = mock(ContractInterface.class);
    ResultOrErrorFuture<ContractInterface> future =
        ResultOrErrorFutureFactory.supply(() -> mockContractInterface);
    when(base.getContractInterfaceFunction()).thenReturn((a) -> future);

    final ContractEitherTemplate contractEitherTemplate = supplyContractEitherTemplate(base);

    final ResultOrError<ContractInterface> contractInterface =
        contractEitherTemplate.getContractInterface(contractAddress);
    assertTrue(contractInterface.hasResult());
    assertEquals(CONTRACT_GETINTERFACE_EITHER,
        ((WithIdentity) contractEitherTemplate.getContractInterfaceFunction()).getIdentity());
  }

  @Test
  public void testExecute() throws Exception {
    final ContractBaseTemplate base = mock(ContractBaseTemplate.class);
    ResultOrErrorFuture<ContractTxHash> future =
        ResultOrErrorFutureFactory
            .supply(() -> new ContractTxHash(of(randomUUID().toString().getBytes())));
    when(base.getExecuteFunction()).thenReturn((a, i, f) -> future);

    final ContractEitherTemplate contractEitherTemplate = supplyContractEitherTemplate(base);

    final Account account = mock(Account.class);
    final ContractFunction contractFunction = new ContractFunction(randomUUID().toString());
    final ResultOrError<ContractTxHash> executionTxHash = contractEitherTemplate
        .execute(account, new ContractInvocation(contractAddress, contractFunction), fee);
    assertTrue(executionTxHash.hasResult());
    assertEquals(CONTRACT_EXECUTE_EITHER,
        ((WithIdentity) contractEitherTemplate.getExecuteFunction()).getIdentity());
  }

  @Test
  public void testQuery() {
    final ContractBaseTemplate base = mock(ContractBaseTemplate.class);
    final ContractResult mockResult = mock(ContractResult.class);
    ResultOrErrorFuture<ContractResult> future =
        ResultOrErrorFutureFactory.supply(() -> mockResult);
    when(base.getQueryFunction()).thenReturn((i) -> future);

    final ContractEitherTemplate contractEitherTemplate = supplyContractEitherTemplate(base);

    final ContractFunction contractFunction = new ContractFunction(randomUUID().toString());
    final ResultOrError<ContractResult> contractResult = contractEitherTemplate
        .query(new ContractInvocation(contractAddress, contractFunction));

    assertTrue(contractResult.hasResult());
    assertEquals(CONTRACT_QUERY_EITHER,
        ((WithIdentity) contractEitherTemplate.getQueryFunction()).getIdentity());
  }

}
