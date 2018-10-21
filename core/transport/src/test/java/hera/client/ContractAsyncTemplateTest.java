/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.model.BytesValue.of;
import static hera.api.tupleorerror.FunctionChain.success;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import com.google.common.util.concurrent.ListenableFuture;
import hera.AbstractTestCase;
import hera.api.encode.Base58WithCheckSum;
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
import hera.api.model.Signature;
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
public class ContractAsyncTemplateTest extends AbstractTestCase {

  protected final AccountAddress accountAddress =
      new AccountAddress(of(new byte[] {AccountAddress.VERSION}));

  protected final ContractAddress contractAddress =
      new ContractAddress(of(new byte[] {AccountAddress.VERSION}));

  protected final Fee fee = Fee.getDefaultFee();

  protected final AergoKeyGenerator generator = new AergoKeyGenerator();

  protected ContractAsyncTemplate supplyContractAsyncTemplate(
      final AergoRPCServiceFutureStub aergoService) {
    final ContractAsyncTemplate contractAsyncTemplate = new ContractAsyncTemplate();
    contractAsyncTemplate.setContext(AergoClientBuilder.getDefaultContext());
    contractAsyncTemplate.aergoService = aergoService;
    return contractAsyncTemplate;
  }

  @Test
  public void testGetReceipt() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture =
        service.submit(() -> Blockchain.Receipt.newBuilder().build());
    when(aergoService.getReceipt(any())).thenReturn(mockListenableFuture);

    final ContractAsyncTemplate contractAsyncTemplate = supplyContractAsyncTemplate(aergoService);

    final ResultOrErrorFuture<ContractTxReceipt> receipt = contractAsyncTemplate
        .getReceipt(new ContractTxHash(of(randomUUID().toString().getBytes())));
    assertTrue(receipt.get().hasResult());
  }

  @Test
  public void testDeployWithClientManagedAccount() throws Exception {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);

    TransactionAsyncTemplate mockTransactionAsyncOperation = mock(TransactionAsyncTemplate.class);
    when(mockTransactionAsyncOperation.commit(any())).thenReturn(ResultOrErrorFutureFactory
        .supply(() -> success(new TxHash(BytesValue.of(randomUUID().toString().getBytes())))));

    final ContractAsyncTemplate contractAsyncTemplate = supplyContractAsyncTemplate(aergoService);
    contractAsyncTemplate.transactionAsyncOperation = mockTransactionAsyncOperation;

    Account account = ClientManagedAccount.of(generator.create());
    Base58WithCheckSum encoded =
        () -> Base58Utils.encodeWithCheck(new byte[] {ContractInterface.PAYLOAD_VERSION});
    final ResultOrErrorFuture<ContractTxHash> deployTxHash =
        contractAsyncTemplate.deploy(account, ContractDefinition.of(encoded), fee);
    assertTrue(deployTxHash.get().hasResult());
  }

  @Test
  public void testDeployWithsServerManagedAccount() throws Exception {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);

    AccountAsyncTemplate mockAccountAsyncOperation = mock(AccountAsyncTemplate.class);
    when(mockAccountAsyncOperation.sign(any(), any()))
        .thenReturn(ResultOrErrorFutureFactory.supply(() -> success(new Signature())));
    TransactionAsyncTemplate mockTransactionAsyncOperation = mock(TransactionAsyncTemplate.class);
    when(mockTransactionAsyncOperation.commit(any())).thenReturn(ResultOrErrorFutureFactory
        .supply(() -> success(new TxHash(BytesValue.of(randomUUID().toString().getBytes())))));

    final ContractAsyncTemplate contractAsyncTemplate = supplyContractAsyncTemplate(aergoService);
    contractAsyncTemplate.accountAsyncOperation = mockAccountAsyncOperation;
    contractAsyncTemplate.transactionAsyncOperation = mockTransactionAsyncOperation;

    Account account = ServerManagedAccount.of(accountAddress);
    Base58WithCheckSum encoded =
        () -> Base58Utils.encodeWithCheck(new byte[] {ContractInterface.PAYLOAD_VERSION});
    final ResultOrErrorFuture<ContractTxHash> deployTxHash =
        contractAsyncTemplate.deploy(account, ContractDefinition.of(encoded), fee);
    assertTrue(deployTxHash.get().hasResult());
  }

  @Test
  public void testGetContractInterface() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture =
        service.submit(() -> Blockchain.ABI.newBuilder().build());
    when(aergoService.getABI(any())).thenReturn(mockListenableFuture);

    final ContractAsyncTemplate contractAsyncTemplate = supplyContractAsyncTemplate(aergoService);

    final ResultOrErrorFuture<ContractInterface> contractInterface =
        contractAsyncTemplate.getContractInterface(contractAddress);
    assertTrue(contractInterface.get().hasResult());
  }

  @Test
  public void testExecuteWithClientManagedAccount() throws Exception {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);

    TransactionAsyncTemplate mockTransactionAsyncOperation = mock(TransactionAsyncTemplate.class);
    when(mockTransactionAsyncOperation.commit(any())).thenReturn(ResultOrErrorFutureFactory
        .supply(() -> success(new TxHash(BytesValue.of(randomUUID().toString().getBytes())))));

    final ContractAsyncTemplate contractAsyncTemplate = supplyContractAsyncTemplate(aergoService);
    contractAsyncTemplate.transactionAsyncOperation = mockTransactionAsyncOperation;

    Account account = ClientManagedAccount.of(generator.create());
    final ResultOrErrorFuture<ContractTxHash> executionTxHash = contractAsyncTemplate
        .execute(account, new ContractInvocation(contractAddress, new ContractFunction()), fee);
    assertTrue(executionTxHash.get().hasResult());
  }

  @Test
  public void testExecuteWithServerManagedAccount() throws Exception {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);

    AccountAsyncTemplate mockAccountAsyncOperation = mock(AccountAsyncTemplate.class);
    when(mockAccountAsyncOperation.sign(any(), any()))
        .thenReturn(ResultOrErrorFutureFactory.supply(() -> success(new Signature())));
    TransactionAsyncTemplate mockTransactionAsyncOperation = mock(TransactionAsyncTemplate.class);
    when(mockTransactionAsyncOperation.commit(any())).thenReturn(ResultOrErrorFutureFactory
        .supply(() -> success(new TxHash(BytesValue.of(randomUUID().toString().getBytes())))));

    final ContractAsyncTemplate contractAsyncTemplate = supplyContractAsyncTemplate(aergoService);
    contractAsyncTemplate.accountAsyncOperation = mockAccountAsyncOperation;
    contractAsyncTemplate.transactionAsyncOperation = mockTransactionAsyncOperation;

    Account account = ServerManagedAccount.of(accountAddress);
    final ResultOrErrorFuture<ContractTxHash> executionTxHash = contractAsyncTemplate
        .execute(account, new ContractInvocation(contractAddress, new ContractFunction()), fee);
    assertTrue(executionTxHash.get().hasResult());
  }

  @Test
  public void testQuery() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = service.submit(() -> SingleBytes.newBuilder().build());
    when(aergoService.queryContract(any())).thenReturn(mockListenableFuture);

    final ContractAsyncTemplate contractAsyncTemplate = supplyContractAsyncTemplate(aergoService);

    final ResultOrErrorFuture<ContractResult> contractResult = contractAsyncTemplate
        .query(new ContractInvocation(contractAddress, new ContractFunction()));

    assertTrue(contractResult.get().hasResult());
  }

}
