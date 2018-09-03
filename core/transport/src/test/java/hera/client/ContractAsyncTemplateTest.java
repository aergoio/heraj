/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.tupleorerror.FunctionChain.success;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import com.google.common.util.concurrent.ListenableFuture;
import hera.AbstractTestCase;
import hera.api.AccountAsyncOperation;
import hera.api.TransactionAsyncOperation;
import hera.api.model.Abi;
import hera.api.model.AbiSet;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.ContractTxHash;
import hera.api.model.Hash;
import hera.api.model.Receipt;
import hera.api.model.Signature;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.transport.ModelConverter;
import hera.util.Base58Utils;
import java.io.ByteArrayInputStream;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain;

@SuppressWarnings({"rawtypes", "unchecked"})
@PrepareForTest({AergoRPCServiceFutureStub.class, Blockchain.Receipt.class})
public class ContractAsyncTemplateTest extends AbstractTestCase {

  protected static final AccountAddress EXECUTOR_ADDRESS =
      AccountAddress.of(randomUUID().toString().getBytes());

  protected static final AccountAddress CONTRACT_ADDRESS =
      AccountAddress.of(randomUUID().toString().getBytes());

  protected static final byte[] CONTRACT_PAYLOAD =
      Base58Utils.encode(randomUUID().toString().getBytes()).getBytes();

  protected static final ModelConverter<Receipt, Blockchain.Receipt> receiptConverter =
      mock(ModelConverter.class);

  protected static final ModelConverter<AbiSet, Blockchain.ABI> abiConverter =
      mock(ModelConverter.class);

  @BeforeClass
  public static void setUpBeforeClass() {
    when(receiptConverter.convertToDomainModel(any(Blockchain.Receipt.class)))
        .thenReturn(mock(Receipt.class));
  }

  @Test
  public void testGetReceipt() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = mock(ListenableFuture.class);
    when(aergoService.getReceipt(any())).thenReturn(mockListenableFuture);

    final ContractAsyncTemplate contractAsyncTemplate =
        new ContractAsyncTemplate(aergoService, mock(AccountAsyncOperation.class),
            mock(TransactionAsyncOperation.class), receiptConverter, abiConverter);

    final ResultOrErrorFuture<Receipt> receipt =
        contractAsyncTemplate.getReceipt(ContractTxHash.of(randomUUID().toString().getBytes()));
    assertNotNull(receipt);
  }

  @Test
  public void testDeploy() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    AccountAsyncOperation mockAccountAsyncOperation = mock(AccountAsyncOperation.class);
    final Account account = new Account();
    account.setNonce(0);
    when(mockAccountAsyncOperation.get(any()))
        .thenReturn(ResultOrErrorFuture.supply(() -> success(account)));
    TransactionAsyncOperation mockTransactionAsyncOperation = mock(TransactionAsyncOperation.class);
    when(mockTransactionAsyncOperation.sign(any()))
        .thenReturn(ResultOrErrorFuture.supply(() -> success(mock(Signature.class))));
    when(mockTransactionAsyncOperation.commit(any()))
        .thenReturn(ResultOrErrorFuture.supply(() -> success(mock(Hash.class))));

    final ContractAsyncTemplate contractAsyncTemplate = new ContractAsyncTemplate(aergoService,
        mockAccountAsyncOperation, mockTransactionAsyncOperation, receiptConverter, abiConverter);

    final ResultOrErrorFuture<ContractTxHash> deployTxHash =
        contractAsyncTemplate.deploy(AccountAddress.of(randomUUID().toString().getBytes()),
            () -> new ByteArrayInputStream(CONTRACT_PAYLOAD));
    assertNotNull(deployTxHash);
  }

  @Test
  public void testGetAbiSet() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = mock(ListenableFuture.class);
    when(aergoService.getABI(any())).thenReturn(mockListenableFuture);

    final ContractAsyncTemplate contractAsyncTemplate =
        new ContractAsyncTemplate(aergoService, mock(AccountAsyncOperation.class),
            mock(TransactionAsyncOperation.class), receiptConverter, abiConverter);

    final ResultOrErrorFuture<AbiSet> abiSet = contractAsyncTemplate.getAbiSet(CONTRACT_ADDRESS);
    assertNotNull(abiSet);
  }

  @Test
  public void testGetAbi() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = mock(ListenableFuture.class);
    when(aergoService.getABI(any())).thenReturn(mockListenableFuture);

    final ContractAsyncTemplate contractAsyncTemplate =
        new ContractAsyncTemplate(aergoService, mock(AccountAsyncOperation.class),
            mock(TransactionAsyncOperation.class), receiptConverter, abiConverter);

    final ResultOrErrorFuture<Abi> abi =
        contractAsyncTemplate.getAbi(CONTRACT_ADDRESS, randomUUID().toString());
    assertNotNull(abi);
  }

  @Test
  public void testExecute() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    AccountAsyncOperation mockAccountAsyncOperation = mock(AccountAsyncOperation.class);
    final Account account = new Account();
    account.setNonce(0);
    when(mockAccountAsyncOperation.get(any()))
        .thenReturn(ResultOrErrorFuture.supply(() -> success(account)));
    TransactionAsyncOperation mockTransactionAsyncOperation = mock(TransactionAsyncOperation.class);
    when(mockTransactionAsyncOperation.sign(any()))
        .thenReturn(ResultOrErrorFuture.supply(() -> success(mock(Signature.class))));
    when(mockTransactionAsyncOperation.commit(any()))
        .thenReturn(ResultOrErrorFuture.supply(() -> success(mock(Hash.class))));

    final ContractAsyncTemplate contractAsyncTemplate = new ContractAsyncTemplate(aergoService,
        mockAccountAsyncOperation, mockTransactionAsyncOperation, receiptConverter, abiConverter);

    final ResultOrErrorFuture<ContractTxHash> executionTxHash =
        contractAsyncTemplate.execute(EXECUTOR_ADDRESS, EXECUTOR_ADDRESS, new Abi(), randomUUID());
    assertNotNull(executionTxHash);
  }

  @Test
  public void testQuery() {
    // TODO server not implemented
  }
}
