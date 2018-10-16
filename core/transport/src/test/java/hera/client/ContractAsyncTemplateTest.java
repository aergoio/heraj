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
import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.api.model.ContractAddress;
import hera.api.model.ContractFunction;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
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

  protected static final AccountAddress EXECUTOR_ADDRESS =
      new AccountAddress(of(new byte[] {AccountAddress.VERSION}));

  protected static final ContractAddress CONTRACT_ADDRESS =
      new ContractAddress(of(new byte[] {AccountAddress.VERSION}));

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
  public void testDeploy() throws Exception {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);

    TransactionAsyncTemplate mockTransactionAsyncOperation = mock(TransactionAsyncTemplate.class);
    when(mockTransactionAsyncOperation.commit(any())).thenReturn(ResultOrErrorFutureFactory
        .supply(() -> success(new TxHash(BytesValue.of(randomUUID().toString().getBytes())))));

    final ContractAsyncTemplate contractAsyncTemplate = supplyContractAsyncTemplate(aergoService);
    contractAsyncTemplate.transactionAsyncOperation = mockTransactionAsyncOperation;

    Base58WithCheckSum encoded =
        () -> Base58Utils.encodeWithCheck(new byte[] {ContractInterface.PAYLOAD_VERSION});
    final ResultOrErrorFuture<ContractTxHash> deployTxHash = contractAsyncTemplate.deploy(
        new AergoKeyGenerator().create(), EXECUTOR_ADDRESS, randomUUID().hashCode(), encoded);
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
        contractAsyncTemplate.getContractInterface(CONTRACT_ADDRESS);
    assertTrue(contractInterface.get().hasResult());
  }

  @Test
  public void testExecute() throws Exception {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);

    TransactionAsyncTemplate mockTransactionAsyncOperation = mock(TransactionAsyncTemplate.class);
    when(mockTransactionAsyncOperation.commit(any())).thenReturn(ResultOrErrorFutureFactory
        .supply(() -> success(new TxHash(BytesValue.of(randomUUID().toString().getBytes())))));

    final ContractAsyncTemplate contractAsyncTemplate = supplyContractAsyncTemplate(aergoService);
    contractAsyncTemplate.transactionAsyncOperation = mockTransactionAsyncOperation;

    final ResultOrErrorFuture<ContractTxHash> executionTxHash = contractAsyncTemplate.execute(
        new AergoKeyGenerator().create(), EXECUTOR_ADDRESS, randomUUID().hashCode(),
        new ContractInvocation(CONTRACT_ADDRESS, new ContractFunction()));
    assertTrue(executionTxHash.get().hasResult());
  }

  @Test
  public void testQuery() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = service.submit(() -> SingleBytes.newBuilder().build());
    when(aergoService.queryContract(any())).thenReturn(mockListenableFuture);

    final ContractAsyncTemplate contractAsyncTemplate = supplyContractAsyncTemplate(aergoService);

    final ResultOrErrorFuture<ContractResult> contractResult = contractAsyncTemplate
        .query(new ContractInvocation(CONTRACT_ADDRESS, new ContractFunction()));

    assertTrue(contractResult.get().hasResult());
  }

}
