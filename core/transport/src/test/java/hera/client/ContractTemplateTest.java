
/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.CONTRACT_DEPLOY;
import static hera.TransportConstants.CONTRACT_EXECUTE;
import static hera.TransportConstants.CONTRACT_GETINTERFACE;
import static hera.TransportConstants.CONTRACT_GETRECEIPT;
import static hera.TransportConstants.CONTRACT_LIST_EVENT;
import static hera.TransportConstants.CONTRACT_QUERY;
import static hera.TransportConstants.CONTRACT_SUBSCRIBE_EVENT;
import static hera.api.model.BytesValue.of;
import static java.util.Collections.emptyList;
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
import hera.api.function.WithIdentity;
import hera.api.model.Account;
import hera.api.model.Aer;
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
import hera.api.model.StreamObserver;
import hera.api.model.Subscription;
import hera.key.AergoKeyGenerator;
import hera.util.Base58Utils;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

@PrepareForTest({ContractBaseTemplate.class})
public class ContractTemplateTest extends AbstractTestCase {

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
    final ContractTxReceipt mockTxReceipt = mock(ContractTxReceipt.class);
    final FinishableFuture<ContractTxReceipt> future = new FinishableFuture<ContractTxReceipt>();
    future.success(mockTxReceipt);
    when(base.getReceiptFunction())
        .thenReturn(new Function1<ContractTxHash, FinishableFuture<ContractTxReceipt>>() {
          @Override
          public FinishableFuture<ContractTxReceipt> apply(ContractTxHash t) {
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
    final FinishableFuture<ContractTxHash> future = new FinishableFuture<ContractTxHash>();
    future.success(new ContractTxHash(of(randomUUID().toString().getBytes())));
    when(base.getDeployFunction()).thenReturn(
        new Function4<Account, ContractDefinition, Long, Fee, FinishableFuture<ContractTxHash>>() {
          @Override
          public FinishableFuture<ContractTxHash> apply(Account t1, ContractDefinition t2,
              Long t3, Fee t4) {
            return future;
          }
        });

    final ContractTemplate contractTemplate = supplyContractTemplate(base);

    Account account = mock(Account.class);
    String encoded = Base58Utils.encodeWithCheck(new byte[] {ContractDefinition.PAYLOAD_VERSION});
    final ContractTxHash deployTxHash =
        contractTemplate.deploy(account, new ContractDefinition(encoded), 0L);
    assertNotNull(deployTxHash);
    assertEquals(CONTRACT_DEPLOY,
        ((WithIdentity) contractTemplate.getDeployFunction()).getIdentity());
  }

  @Test
  public void testGetContractInterface() {
    final ContractBaseTemplate base = mock(ContractBaseTemplate.class);
    final ContractInterface mockContractInterface = mock(ContractInterface.class);
    final FinishableFuture<ContractInterface> future = new FinishableFuture<ContractInterface>();
    future.success(mockContractInterface);
    when(base.getContractInterfaceFunction())
        .thenReturn(new Function1<ContractAddress, FinishableFuture<ContractInterface>>() {
          @Override
          public FinishableFuture<ContractInterface> apply(ContractAddress t) {
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
    final FinishableFuture<ContractTxHash> future = new FinishableFuture<ContractTxHash>();
    future.success(new ContractTxHash(of(randomUUID().toString().getBytes())));
    when(base.getExecuteFunction()).thenReturn(
        new Function4<Account, ContractInvocation, Long, Fee, FinishableFuture<ContractTxHash>>() {
          @Override
          public FinishableFuture<ContractTxHash> apply(Account t1, ContractInvocation t2,
              Long t3, Fee t4) {
            return future;
          }
        });

    final ContractTemplate contractTemplate = supplyContractTemplate(base);

    final Account account = mock(Account.class);
    final ContractFunction contractFunction = new ContractFunction(randomUUID().toString());
    final ContractTxHash executionTxHash = contractTemplate
        .execute(account,
            new ContractInvocation(contractAddress, contractFunction, emptyList(), Aer.ZERO), 0L);
    assertNotNull(executionTxHash);
    assertEquals(CONTRACT_EXECUTE,
        ((WithIdentity) contractTemplate.getExecuteFunction()).getIdentity());
  }

  @Test
  public void testQuery() {
    final ContractBaseTemplate base = mock(ContractBaseTemplate.class);
    final ContractResult mockResult = mock(ContractResult.class);
    final FinishableFuture<ContractResult> future = new FinishableFuture<ContractResult>();
    future.success(mockResult);
    when(base.getQueryFunction())
        .thenReturn(new Function1<ContractInvocation, FinishableFuture<ContractResult>>() {
          @Override
          public FinishableFuture<ContractResult> apply(ContractInvocation t) {
            return future;
          }
        });

    final ContractTemplate contractTemplate = supplyContractTemplate(base);

    final ContractFunction contractFunction = new ContractFunction(randomUUID().toString());
    final ContractResult contractResult = contractTemplate
        .query(new ContractInvocation(contractAddress, contractFunction, emptyList(), Aer.ZERO));

    assertNotNull(contractResult);
    assertEquals(CONTRACT_QUERY,
        ((WithIdentity) contractTemplate.getQueryFunction()).getIdentity());
  }

  @Test
  public void testListEvent() {
    final ContractBaseTemplate base = mock(ContractBaseTemplate.class);
    final List<Event> mockEventList = new ArrayList<Event>();
    final FinishableFuture<List<Event>> future = new FinishableFuture<List<Event>>();
    future.success(mockEventList);
    when(base.getListEventFunction())
        .thenReturn(new Function1<EventFilter, FinishableFuture<List<Event>>>() {

          @Override
          public FinishableFuture<List<Event>> apply(EventFilter t) {
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
    when(base.getSubscribeEventFunction())
        .thenReturn(new Function2<EventFilter, StreamObserver<Event>, Subscription<Event>>() {

          @Override
          public Subscription<Event> apply(EventFilter t1, StreamObserver<Event> t2) {
            return mockSubscription;
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
