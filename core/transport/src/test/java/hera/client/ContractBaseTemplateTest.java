/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import com.google.common.util.concurrent.ListenableFuture;
import hera.AbstractTestCase;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.api.model.ClientManagedAccount;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractFunction;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.model.Fee;
import hera.api.model.ServerManagedAccount;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.key.AergoKeyGenerator;
import hera.util.Base58Utils;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain;
import types.Rpc.SingleBytes;

@SuppressWarnings({"rawtypes", "unchecked"})
@PrepareForTest({AergoRPCServiceFutureStub.class})
public class ContractBaseTemplateTest extends AbstractTestCase {

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

  protected ContractBaseTemplate supplyContractBaseTemplate(
      final AergoRPCServiceFutureStub aergoService) {
    final ContractBaseTemplate contractBaseTemplate = new ContractBaseTemplate();
    contractBaseTemplate.aergoService = aergoService;
    return contractBaseTemplate;
  }

  @Test
  public void testGetReceipt() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture =
        service.submit(() -> Blockchain.Receipt.newBuilder().build());
    when(aergoService.getReceipt(any())).thenReturn(mockListenableFuture);

    final ContractBaseTemplate contractBaseTemplate = supplyContractBaseTemplate(aergoService);

    final ResultOrErrorFuture<ContractTxReceipt> receipt = contractBaseTemplate
        .getReceiptFunction().apply(new ContractTxHash(of(randomUUID().toString().getBytes())));
    assertTrue(receipt.get().hasResult());
  }

  @Test
  public void testDeployWithClientManagedAccount() throws Exception {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);

    TransactionBaseTemplate mockTransactionBaseTemplate = mock(TransactionBaseTemplate.class);
    when(mockTransactionBaseTemplate.getCommitFunction()).thenReturn(t -> ResultOrErrorFutureFactory
        .supply(() -> new TxHash(BytesValue.of(randomUUID().toString().getBytes()))));

    final ContractBaseTemplate contractBaseTemplate = supplyContractBaseTemplate(aergoService);
    contractBaseTemplate.transactionBaseTemplate = mockTransactionBaseTemplate;

    Account account = ClientManagedAccount.of(generator.create());
    String encoded = Base58Utils.encodeWithCheck(new byte[] {ContractDefinition.PAYLOAD_VERSION});
    final ResultOrErrorFuture<ContractTxHash> deployTxHash =
        contractBaseTemplate.getDeployFunction().apply(account, ContractDefinition.of(encoded),
            fee);
    assertTrue(deployTxHash.get().hasResult());
  }

  @Test
  public void testDeployWithsServerManagedAccount() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);

    AccountBaseTemplate mockAccountBaseTemplate = mock(AccountBaseTemplate.class);
    Transaction mockSignedTransaction = mock(Transaction.class);
    when(mockAccountBaseTemplate.getSignFunction())
        .thenReturn((a, t) -> ResultOrErrorFutureFactory.supply(() -> mockSignedTransaction));
    TransactionBaseTemplate mockTransactionBaseTemplate = mock(TransactionBaseTemplate.class);
    when(mockTransactionBaseTemplate.getCommitFunction()).thenReturn(t -> ResultOrErrorFutureFactory
        .supply(() -> new TxHash(BytesValue.of(randomUUID().toString().getBytes()))));

    final ContractBaseTemplate contractBaseTemplate = supplyContractBaseTemplate(aergoService);
    contractBaseTemplate.accountBaseTemplate = mockAccountBaseTemplate;
    contractBaseTemplate.transactionBaseTemplate = mockTransactionBaseTemplate;

    Account account = ServerManagedAccount.of(accountAddress);
    String encoded = Base58Utils.encodeWithCheck(new byte[] {ContractDefinition.PAYLOAD_VERSION});
    final ResultOrErrorFuture<ContractTxHash> deployTxHash =
        contractBaseTemplate.getDeployFunction().apply(account, ContractDefinition.of(encoded),
            fee);
    assertTrue(deployTxHash.get().hasResult());
  }

  @Test
  public void testGetContractInterface() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture =
        service.submit(() -> Blockchain.ABI.newBuilder().build());
    when(aergoService.getABI(any())).thenReturn(mockListenableFuture);

    final ContractBaseTemplate contractBaseTemplate = supplyContractBaseTemplate(aergoService);

    final ResultOrErrorFuture<ContractInterface> contractInterface =
        contractBaseTemplate.getContractInterfaceFunction().apply(contractAddress);
    assertTrue(contractInterface.get().hasResult());
  }

  @Test
  public void testExecuteWithClientManagedAccount() throws Exception {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);

    TransactionBaseTemplate mockTransactionBaseTemplate = mock(TransactionBaseTemplate.class);
    when(mockTransactionBaseTemplate.getCommitFunction()).thenReturn(t -> ResultOrErrorFutureFactory
        .supply(() -> new TxHash(BytesValue.of(randomUUID().toString().getBytes()))));

    final ContractBaseTemplate contractBaseTemplate = supplyContractBaseTemplate(aergoService);
    contractBaseTemplate.transactionBaseTemplate = mockTransactionBaseTemplate;

    final Account account = ClientManagedAccount.of(generator.create());
    final ContractFunction contractFunction = new ContractFunction(randomUUID().toString());
    final ResultOrErrorFuture<ContractTxHash> executionTxHash = contractBaseTemplate
        .getExecuteFunction()
        .apply(account, new ContractInvocation(contractAddress, contractFunction), fee);
    assertTrue(executionTxHash.get().hasResult());
  }

  @Test
  public void testExecuteWithServerManagedAccount() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);

    AccountBaseTemplate mockAccountBaseTemplate = mock(AccountBaseTemplate.class);
    Transaction mockSignedTransaction = mock(Transaction.class);
    when(mockAccountBaseTemplate.getSignFunction())
        .thenReturn((a, t) -> ResultOrErrorFutureFactory.supply(() -> mockSignedTransaction));
    TransactionBaseTemplate mockTransactionBaseTemplate = mock(TransactionBaseTemplate.class);
    when(mockTransactionBaseTemplate.getCommitFunction()).thenReturn(t -> ResultOrErrorFutureFactory
        .supply(() -> new TxHash(BytesValue.of(randomUUID().toString().getBytes()))));

    final ContractBaseTemplate contractBaseTemplate = supplyContractBaseTemplate(aergoService);
    contractBaseTemplate.accountBaseTemplate = mockAccountBaseTemplate;
    contractBaseTemplate.transactionBaseTemplate = mockTransactionBaseTemplate;

    final Account account = ServerManagedAccount.of(accountAddress);
    final ContractFunction contractFunction = new ContractFunction(randomUUID().toString());
    final ResultOrErrorFuture<ContractTxHash> executionTxHash = contractBaseTemplate
        .getExecuteFunction()
        .apply(account, new ContractInvocation(contractAddress, contractFunction), fee);
    assertTrue(executionTxHash.get().hasResult());
  }

  @Test
  public void testQuery() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = service.submit(() -> SingleBytes.newBuilder().build());
    when(aergoService.queryContract(any())).thenReturn(mockListenableFuture);

    final ContractBaseTemplate contractBaseTemplate = supplyContractBaseTemplate(aergoService);

    final ContractFunction contractFunction = new ContractFunction(randomUUID().toString());
    final ResultOrErrorFuture<ContractResult> contractResult = contractBaseTemplate
        .getQueryFunction().apply(new ContractInvocation(contractAddress, contractFunction));

    assertTrue(contractResult.get().hasResult());
  }

}
