/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.internal;

import static hera.api.model.BytesValue.of;
import static hera.util.TransportUtils.copyFrom;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.ByteString;
import hera.AbstractTestCase;
import hera.Context;
import hera.ContextProvider;
import hera.api.function.Function1;
import hera.api.model.BytesValue;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractFunction;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.model.Event;
import hera.api.model.EventFilter;
import hera.api.model.Fee;
import hera.api.model.StateVariable;
import hera.api.model.Subscription;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.key.AergoKeyGenerator;
import hera.key.Signer;
import hera.spec.resolver.ContractDefinitionSpec;
import hera.util.Base58Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.AergoRPCServiceGrpc.AergoRPCServiceStub;
import types.Blockchain;
import types.Rpc;

@PrepareForTest({AergoRPCServiceFutureStub.class, AergoRPCServiceStub.class})
public class ContractBaseTemplateTest extends AbstractTestCase {

  protected final String encodedContract =
      Base58Utils.encodeWithCheck(new byte[] {ContractDefinitionSpec.PAYLOAD_VERSION});

  protected ContractInterface contractInterface;

  protected String functionName = randomUUID().toString();

  protected final Fee fee = Fee.ZERO;

  protected final AergoKeyGenerator generator = new AergoKeyGenerator();

  @Override
  public void setUp() {
    super.setUp();
    final List<ContractFunction> functions = new ArrayList<ContractFunction>();
    functions.add(new ContractFunction(functionName));
    this.contractInterface = new ContractInterface(ContractAddress.EMPTY, "", "",
        functions, new ArrayList<StateVariable>());
  }

  protected ContractBaseTemplate supplyContractBaseTemplate(
      final AergoRPCServiceFutureStub futureService) {
    final ContractBaseTemplate contractBaseTemplate = new ContractBaseTemplate();
    contractBaseTemplate.futureService = futureService;
    contractBaseTemplate.contextProvider = new ContextProvider() {
      @Override
      public Context get() {
        return context;
      }
    };
    return contractBaseTemplate;
  }

  protected ContractBaseTemplate supplyContractBaseTemplate(
      final AergoRPCServiceStub streamService) {
    final ContractBaseTemplate contractBaseTemplate = new ContractBaseTemplate();
    contractBaseTemplate.streamService = streamService;
    contractBaseTemplate.contextProvider = new ContextProvider() {
      @Override
      public Context get() {
        return context;
      }
    };
    return contractBaseTemplate;
  }

  @Test
  public void testGetReceipt() {
    final AergoRPCServiceFutureStub futureService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<Blockchain.Receipt> mockListenableFuture =
        service.submit(new Callable<Blockchain.Receipt>() {
          @Override
          public Blockchain.Receipt call() throws Exception {
            final ByteString rpcAddress = copyFrom(contractAddress.getBytesValue());
            return Blockchain.Receipt.newBuilder().setContractAddress(rpcAddress).build();
          }
        });
    when(futureService.getReceipt(any(Rpc.SingleBytes.class))).thenReturn(mockListenableFuture);

    final ContractBaseTemplate contractBaseTemplate = supplyContractBaseTemplate(futureService);

    final FinishableFuture<ContractTxReceipt> receipt = contractBaseTemplate
        .getReceiptFunction().apply(new ContractTxHash(of(randomUUID().toString().getBytes())));
    assertNotNull(receipt.get());
  }

  @Test
  public void testDeploy() {
    final AergoRPCServiceFutureStub futureService = mock(AergoRPCServiceFutureStub.class);

    TransactionBaseTemplate mockTransactionBaseTemplate = mock(TransactionBaseTemplate.class);
    when(mockTransactionBaseTemplate.getCommitFunction())
        .thenReturn(new Function1<Transaction, FinishableFuture<TxHash>>() {
          @Override
          public FinishableFuture<TxHash> apply(Transaction t) {
            final FinishableFuture<TxHash> future = new FinishableFuture<TxHash>();
            future.success(new TxHash(BytesValue.of(randomUUID().toString().getBytes())));
            return future;
          }
        });

    final ContractBaseTemplate contractBaseTemplate = supplyContractBaseTemplate(futureService);
    contractBaseTemplate.transactionBaseTemplate = mockTransactionBaseTemplate;

    Signer signer = new AergoKeyGenerator().create();
    final FinishableFuture<ContractTxHash> deployTxHash = contractBaseTemplate
        .getDeployFunction()
        .apply(signer, ContractDefinition.newBuilder().encodedContract(encodedContract).build(),
            0L, fee);
    assertNotNull(deployTxHash.get());
  }

  @Test
  public void testReDeploy() {
    final AergoRPCServiceFutureStub futureService = mock(AergoRPCServiceFutureStub.class);

    TransactionBaseTemplate mockTransactionBaseTemplate = mock(TransactionBaseTemplate.class);
    when(mockTransactionBaseTemplate.getCommitFunction())
        .thenReturn(new Function1<Transaction, FinishableFuture<TxHash>>() {
          @Override
          public FinishableFuture<TxHash> apply(Transaction t) {
            final FinishableFuture<TxHash> future = new FinishableFuture<TxHash>();
            future.success(new TxHash(BytesValue.of(randomUUID().toString().getBytes())));
            return future;
          }
        });

    final ContractBaseTemplate contractBaseTemplate = supplyContractBaseTemplate(futureService);
    contractBaseTemplate.transactionBaseTemplate = mockTransactionBaseTemplate;

    final Signer signer = new AergoKeyGenerator().create();
    final FinishableFuture<ContractTxHash> deployTxHash = contractBaseTemplate
        .getReDeployFunction()
        .apply(signer, contractAddress,
            ContractDefinition.newBuilder().encodedContract(encodedContract).build(),
            0L, fee);
    assertNotNull(deployTxHash.get());
  }

  @Test
  public void testGetContractInterface() {
    final AergoRPCServiceFutureStub futureService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<Blockchain.ABI> mockListenableFuture =
        service.submit(new Callable<Blockchain.ABI>() {
          @Override
          public Blockchain.ABI call() throws Exception {
            return Blockchain.ABI.newBuilder().build();
          }
        });
    when(futureService.getABI(any(Rpc.SingleBytes.class))).thenReturn(mockListenableFuture);

    final ContractBaseTemplate contractBaseTemplate = supplyContractBaseTemplate(futureService);

    final FinishableFuture<ContractInterface> contractInterface =
        contractBaseTemplate.getContractInterfaceFunction().apply(contractAddress);
    assertNotNull(contractInterface.get());
  }

  @Test
  public void testExecute() {
    final AergoRPCServiceFutureStub futureService = mock(AergoRPCServiceFutureStub.class);

    TransactionBaseTemplate mockTransactionBaseTemplate = mock(TransactionBaseTemplate.class);
    when(mockTransactionBaseTemplate.getCommitFunction())
        .thenReturn(new Function1<Transaction, FinishableFuture<TxHash>>() {
          @Override
          public FinishableFuture<TxHash> apply(Transaction t) {
            final FinishableFuture<TxHash> future = new FinishableFuture<TxHash>();
            future.success(new TxHash(BytesValue.of(randomUUID().toString().getBytes())));
            return future;
          }
        });

    final ContractBaseTemplate contractBaseTemplate = supplyContractBaseTemplate(futureService);
    contractBaseTemplate.transactionBaseTemplate = mockTransactionBaseTemplate;

    final Signer signer = new AergoKeyGenerator().create();

    final ContractInvocation invocation =
        contractInterface.newInvocationBuilder().function(functionName).build();
    final FinishableFuture<ContractTxHash> executionTxHash =
        contractBaseTemplate.getExecuteFunction().apply(signer, invocation, 0L, fee);
    assertNotNull(executionTxHash.get());
  }

  @Test
  public void testQuery() {
    final AergoRPCServiceFutureStub futureService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<Rpc.SingleBytes> mockListenableFuture =
        service.submit(new Callable<Rpc.SingleBytes>() {
          @Override
          public Rpc.SingleBytes call() throws Exception {
            return Rpc.SingleBytes.newBuilder().build();
          }
        });
    when(futureService.queryContract(any(Blockchain.Query.class))).thenReturn(mockListenableFuture);

    final ContractBaseTemplate contractBaseTemplate = supplyContractBaseTemplate(futureService);

    final ContractInvocation invocation =
        contractInterface.newInvocationBuilder().function(functionName).build();
    final FinishableFuture<ContractResult> contractResult = contractBaseTemplate
        .getQueryFunction().apply(invocation);

    assertNotNull(contractResult.get());
  }

  @Test
  public void testListEvents() {
    final AergoRPCServiceFutureStub futureService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<Rpc.EventList> mockListenableFuture =
        service.submit(new Callable<Rpc.EventList>() {
          @Override
          public Rpc.EventList call() throws Exception {
            return Rpc.EventList.newBuilder().build();
          }
        });
    when(futureService.listEvents(any(Blockchain.FilterInfo.class)))
        .thenReturn(mockListenableFuture);

    final ContractBaseTemplate contractBaseTemplate = supplyContractBaseTemplate(futureService);

    final EventFilter eventFilter = EventFilter.newBuilder(contractAddress).build();
    final FinishableFuture<List<Event>> events =
        contractBaseTemplate.getListEventFunction().apply(eventFilter);
    assertNotNull(events);
  }

  @Test
  public void testSubscribeEvent() {
    final AergoRPCServiceStub streamService = mock(AergoRPCServiceStub.class);
    final ContractBaseTemplate contractBaseTemplate = supplyContractBaseTemplate(streamService);

    final EventFilter eventFilter = EventFilter.newBuilder(contractAddress).build();
    final Subscription<Event> subscription =
        contractBaseTemplate.getSubscribeEventFunction().apply(eventFilter, null);
    assertNotNull(subscription);
  }

}
