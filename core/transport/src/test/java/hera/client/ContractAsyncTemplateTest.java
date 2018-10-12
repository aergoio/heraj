/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.model.BytesValue.of;
import static hera.api.tupleorerror.FunctionChain.success;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.ByteString;
import hera.AbstractTestCase;
import hera.Context;
import hera.api.AccountAsyncOperation;
import hera.api.SignAsyncOperation;
import hera.api.TransactionAsyncOperation;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.ContractAddress;
import hera.api.model.ContractFunction;
import hera.api.model.ContractInterface;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.model.Signature;
import hera.api.model.TxHash;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.transport.ModelConverter;
import hera.util.Base58Utils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain;
import types.Rpc;

@SuppressWarnings({"rawtypes", "unchecked"})
@PrepareForTest({AergoRPCServiceFutureStub.class, Blockchain.Receipt.class, Rpc.SingleBytes.class})
public class ContractAsyncTemplateTest extends AbstractTestCase {

  protected static final AccountAddress EXECUTOR_ADDRESS =
      new AccountAddress(of(new byte[] {AccountAddress.VERSION}));

  protected static final ContractAddress CONTRACT_ADDRESS =
      new ContractAddress(of(new byte[] {AccountAddress.VERSION}));

  protected static final byte[] CONTRACT_PAYLOAD =
      Base58Utils.encode(randomUUID().toString().getBytes()).getBytes();

  protected static final Context context = AergoClientBuilder.getDefaultContext();

  protected static final ModelConverter<AccountAddress, ByteString> accountAddressConverter =
      mock(ModelConverter.class);

  protected static final ModelConverter<ContractTxReceipt, Blockchain.Receipt> receiptConverter =
      mock(ModelConverter.class);

  protected static final ModelConverter<ContractInterface, Blockchain.ABI> interfaceConverter =
      mock(ModelConverter.class);

  protected static final ModelConverter<ContractResult, Rpc.SingleBytes> contractResultConverter =
      mock(ModelConverter.class);

  @BeforeClass
  public static void setUpBeforeClass() {
    when(accountAddressConverter.convertToRpcModel(any(AccountAddress.class)))
        .thenReturn(mock(ByteString.class));
    when(accountAddressConverter.convertToDomainModel(any(ByteString.class)))
        .thenReturn(mock(AccountAddress.class));
    when(receiptConverter.convertToDomainModel(any(Blockchain.Receipt.class)))
        .thenReturn(mock(ContractTxReceipt.class));
    when(contractResultConverter.convertToDomainModel(any(Rpc.SingleBytes.class)))
        .thenReturn(mock(ContractResult.class));
  }

  @Test
  public void testGetReceipt() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = mock(ListenableFuture.class);
    when(aergoService.getReceipt(any())).thenReturn(mockListenableFuture);

    final ContractAsyncTemplate contractAsyncTemplate =
        new ContractAsyncTemplate(aergoService, context, mock(SignAsyncOperation.class),
            mock(AccountAsyncOperation.class), mock(TransactionAsyncOperation.class),
            accountAddressConverter, receiptConverter, interfaceConverter, contractResultConverter);

    final ResultOrErrorFuture<ContractTxReceipt> receipt = contractAsyncTemplate
        .getReceipt(new ContractTxHash(of(randomUUID().toString().getBytes())));
    assertNotNull(receipt);
  }

  @Test
  public void testDeploy() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    SignAsyncOperation mockSignAsyncOperation = mock(SignAsyncOperation.class);
    when(mockSignAsyncOperation.sign(any(), any()))
        .thenReturn(ResultOrErrorFutureFactory.supply(() -> success(mock(Signature.class))));

    AccountAsyncOperation mockAccountAsyncOperation = mock(AccountAsyncOperation.class);
    final Account account = new Account();
    account.setNonce(0);
    when(mockAccountAsyncOperation.get(any(AccountAddress.class)))
        .thenReturn(ResultOrErrorFutureFactory.supply(() -> success(account)));
    TransactionAsyncOperation mockTransactionAsyncOperation = mock(TransactionAsyncOperation.class);
    when(mockTransactionAsyncOperation.commit(any()))
        .thenReturn(ResultOrErrorFutureFactory.supply(() -> success(mock(TxHash.class))));

    final ContractAsyncTemplate contractAsyncTemplate = new ContractAsyncTemplate(aergoService,
        context, mockSignAsyncOperation, mockAccountAsyncOperation, mockTransactionAsyncOperation,
        accountAddressConverter, receiptConverter, interfaceConverter, contractResultConverter);

    final ResultOrErrorFuture<ContractTxHash> deployTxHash =
        contractAsyncTemplate.deploy(EXECUTOR_ADDRESS, () -> CONTRACT_PAYLOAD);
    assertNotNull(deployTxHash);
  }

  @Test
  public void testGetContractInterface() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = mock(ListenableFuture.class);
    when(aergoService.getABI(any())).thenReturn(mockListenableFuture);

    final ContractAsyncTemplate contractAsyncTemplate =
        new ContractAsyncTemplate(aergoService, context, mock(SignAsyncOperation.class),
            mock(AccountAsyncOperation.class), mock(TransactionAsyncOperation.class),
            accountAddressConverter, receiptConverter, interfaceConverter, contractResultConverter);

    final ResultOrErrorFuture<ContractInterface> abiSet =
        contractAsyncTemplate.getContractInterface(CONTRACT_ADDRESS);
    assertNotNull(abiSet);
  }

  @Test
  public void testExecute() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);

    SignAsyncOperation mockSignAsyncOperation = mock(SignAsyncOperation.class);
    when(mockSignAsyncOperation.sign(any(), any()))
        .thenReturn(ResultOrErrorFutureFactory.supply(() -> success(mock(Signature.class))));

    AccountAsyncOperation mockAccountAsyncOperation = mock(AccountAsyncOperation.class);
    final Account account = new Account();
    account.setNonce(0);
    when(mockAccountAsyncOperation.get(any(AccountAddress.class)))
        .thenReturn(ResultOrErrorFutureFactory.supply(() -> success(account)));
    TransactionAsyncOperation mockTransactionAsyncOperation = mock(TransactionAsyncOperation.class);
    when(mockTransactionAsyncOperation.commit(any()))
        .thenReturn(ResultOrErrorFutureFactory.supply(() -> success(mock(TxHash.class))));

    final ContractAsyncTemplate contractAsyncTemplate = new ContractAsyncTemplate(aergoService,
        context, mockSignAsyncOperation, mockAccountAsyncOperation, mockTransactionAsyncOperation,
        accountAddressConverter, receiptConverter, interfaceConverter, contractResultConverter);

    final ResultOrErrorFuture<ContractTxHash> executionTxHash = contractAsyncTemplate
        .execute(EXECUTOR_ADDRESS, CONTRACT_ADDRESS, new ContractFunction(), randomUUID());
    assertNotNull(executionTxHash);
  }

  @Test
  public void testQuery() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = mock(ListenableFuture.class);
    when(aergoService.queryContract(any())).thenReturn(mockListenableFuture);

    final ContractAsyncTemplate contractAsyncTemplate =
        new ContractAsyncTemplate(aergoService, context, mock(SignAsyncOperation.class),
            mock(AccountAsyncOperation.class), mock(TransactionAsyncOperation.class),
            accountAddressConverter, receiptConverter, interfaceConverter, contractResultConverter);

    final ResultOrErrorFuture<ContractResult> contractResult =
        contractAsyncTemplate.query(CONTRACT_ADDRESS, new ContractFunction());
    assertNotNull(contractResult);
  }
}
