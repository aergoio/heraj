
/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.model.BytesValue.of;
import static hera.client.ClientConstants.CONTRACT_DEPLOY;
import static hera.client.ClientConstants.CONTRACT_EXECUTE;
import static hera.client.ClientConstants.CONTRACT_GETINTERFACE;
import static hera.client.ClientConstants.CONTRACT_GETRECEIPT;
import static hera.client.ClientConstants.CONTRACT_LIST_EVENT;
import static hera.client.ClientConstants.CONTRACT_QUERY;
import static hera.client.ClientConstants.CONTRACT_REDEPLOY;
import static hera.client.ClientConstants.CONTRACT_SUBSCRIBE_EVENT;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import hera.AbstractTestCase;
import hera.ContextProvider;
import hera.api.function.Function1;
import hera.api.function.Function2;
import hera.api.function.Function4;
import hera.api.function.Function5;
import hera.api.function.WithIdentity;
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
import hera.api.model.StreamObserver;
import hera.api.model.Subscription;
import hera.client.internal.ContractBaseTemplate;
import hera.client.internal.HerajFutures;
import hera.key.AergoKeyGenerator;
import hera.key.Signer;
import hera.spec.resolver.ContractDefinitionSpec;
import hera.util.Base58Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

@PrepareForTest({ContractBaseTemplate.class})
public class ContractTemplateTest extends AbstractTestCase {

  protected final String encodedContract =
      Base58Utils.encodeWithCheck(new byte[] {ContractDefinitionSpec.PAYLOAD_VERSION});

  protected ContractInterface contractInterface;

  protected String functionName = randomUUID().toString();

  protected final AergoKeyGenerator generator = new AergoKeyGenerator();

  @Override
  public void setUp() {
    super.setUp();
    final List<ContractFunction> functions = new ArrayList<ContractFunction>();
    functions.add(new ContractFunction(functionName));
    this.contractInterface = new ContractInterface(ContractAddress.EMPTY, "", "",
        functions, new ArrayList<StateVariable>());
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
    final Future<ContractTxReceipt> future =
        HerajFutures.success(ContractTxReceipt.newBuilder().build());
    when(base.getReceiptFunction())
        .thenReturn(new Function1<ContractTxHash, Future<ContractTxReceipt>>() {
          @Override
          public Future<ContractTxReceipt> apply(ContractTxHash t) {
            return future;
          }
        });

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
    final Future<ContractTxHash> future =
        HerajFutures.success(new ContractTxHash(of(randomUUID().toString().getBytes())));
    when(base.getDeployFunction()).thenReturn(
        new Function4<Signer, ContractDefinition, Long, Fee, Future<ContractTxHash>>() {
          @Override
          public Future<ContractTxHash> apply(Signer t1, ContractDefinition t2,
              Long t3, Fee t4) {
            return future;
          }
        });

    final ContractTemplate contractTemplate = supplyContractTemplate(base);

    Signer signer = new AergoKeyGenerator().create();
    final ContractDefinition definition =
        ContractDefinition.newBuilder().encodedContract(encodedContract).build();
    final ContractTxHash deployTxHash = contractTemplate.deploy(signer, definition, 0L, Fee.ZERO);
    assertNotNull(deployTxHash);
    assertEquals(CONTRACT_DEPLOY,
        ((WithIdentity) contractTemplate.getDeployFunction()).getIdentity());
  }

  @Test
  public void testReDeploy() {
    final ContractBaseTemplate base = mock(ContractBaseTemplate.class);
    final Future<ContractTxHash> future =
        HerajFutures.success(new ContractTxHash(of(randomUUID().toString().getBytes())));
    when(base.getReDeployFunction()).thenReturn(new Function5<Signer, ContractAddress,
        ContractDefinition, Long, Fee, Future<ContractTxHash>>() {

      @Override
      public Future<ContractTxHash> apply(Signer t1, ContractAddress t2,
          ContractDefinition t3, Long t4, Fee t5) {
        return future;
      }
    });

    final ContractTemplate contractTemplate = supplyContractTemplate(base);

    final Signer signer = new AergoKeyGenerator().create();
    final ContractDefinition definition =
        ContractDefinition.newBuilder().encodedContract(encodedContract).build();
    final ContractTxHash deployTxHash =
        contractTemplate.redeploy(signer, contractAddress, definition, 0L, Fee.ZERO);
    assertNotNull(deployTxHash);
    assertEquals(CONTRACT_REDEPLOY,
        ((WithIdentity) contractTemplate.getReDeployFunction()).getIdentity());
  }

  @Test
  public void testGetContractInterface() {
    final ContractBaseTemplate base = mock(ContractBaseTemplate.class);
    final ContractInterface mockContractInterface = mock(ContractInterface.class);
    final Future<ContractInterface> future = HerajFutures.success(mockContractInterface);
    when(base.getContractInterfaceFunction())
        .thenReturn(new Function1<ContractAddress, Future<ContractInterface>>() {
          @Override
          public Future<ContractInterface> apply(ContractAddress t) {
            return future;
          }
        });

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
    final Future<ContractTxHash> future =
        HerajFutures.success(new ContractTxHash(of(randomUUID().toString().getBytes())));
    when(base.getExecuteFunction()).thenReturn(
        new Function4<Signer, ContractInvocation, Long, Fee, Future<ContractTxHash>>() {
          @Override
          public Future<ContractTxHash> apply(Signer t1, ContractInvocation t2,
              Long t3, Fee t4) {
            return future;
          }
        });

    final ContractTemplate contractTemplate = supplyContractTemplate(base);

    final Signer signer = new AergoKeyGenerator().create();
    final ContractInvocation invocation =
        contractInterface.newInvocationBuilder().function(functionName).build();
    final ContractTxHash executionTxHash =
        contractTemplate.execute(signer, invocation, 0L, Fee.ZERO);
    assertNotNull(executionTxHash);
    assertEquals(CONTRACT_EXECUTE,
        ((WithIdentity) contractTemplate.getExecuteFunction()).getIdentity());
  }

  @Test
  public void testQuery() {
    final ContractBaseTemplate base = mock(ContractBaseTemplate.class);
    final ContractResult mockResult = mock(ContractResult.class);
    final Future<ContractResult> future = HerajFutures.success(mockResult);
    when(base.getQueryFunction())
        .thenReturn(new Function1<ContractInvocation, Future<ContractResult>>() {
          @Override
          public Future<ContractResult> apply(ContractInvocation t) {
            return future;
          }
        });

    final ContractTemplate contractTemplate = supplyContractTemplate(base);

    final ContractInvocation invocation =
        contractInterface.newInvocationBuilder().function(functionName).build();
    final ContractResult contractResult = contractTemplate.query(invocation);

    assertNotNull(contractResult);
    assertEquals(CONTRACT_QUERY,
        ((WithIdentity) contractTemplate.getQueryFunction()).getIdentity());
  }

  @Test
  public void testListEvent() {
    final ContractBaseTemplate base = mock(ContractBaseTemplate.class);
    final List<Event> list = new ArrayList<Event>();
    final Future<List<Event>> future = HerajFutures.success(list);
    when(base.getListEventFunction())
        .thenReturn(new Function1<EventFilter, Future<List<Event>>>() {

          @Override
          public Future<List<Event>> apply(EventFilter t) {
            return future;
          }
        });

    final ContractTemplate contractTemplate = supplyContractTemplate(base);

    final EventFilter eventFilter = EventFilter.newBuilder(contractAddress).build();
    final List<Event> events = contractTemplate.listEvents(eventFilter);
    assertNotNull(events);
    assertEquals(CONTRACT_LIST_EVENT,
        ((WithIdentity) contractTemplate.getListEventFunction()).getIdentity());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testSubscribeEvent() {
    final ContractBaseTemplate base = mock(ContractBaseTemplate.class);
    final Subscription<Event> mockSubscription = mock(Subscription.class);
    final Future<Subscription<Event>> future = HerajFutures.success(mockSubscription);
    when(base.getSubscribeEventFunction())
        .thenReturn(new Function2<EventFilter, StreamObserver<Event>,
            Future<Subscription<Event>>>() {

          @Override
          public Future<Subscription<Event>> apply(EventFilter t1,
              StreamObserver<Event> t2) {
            return future;
          }
        });

    final ContractTemplate contractTemplate = supplyContractTemplate(base);

    final EventFilter eventFilter = EventFilter.newBuilder(contractAddress).build();
    final Subscription<Event> subscription = contractTemplate.subscribeEvent(eventFilter, null);
    assertNotNull(subscription);
    assertEquals(CONTRACT_SUBSCRIBE_EVENT,
        ((WithIdentity) contractTemplate.getSubscribeEventFunction()).getIdentity());
  }

}
