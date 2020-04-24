/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.Context;
import hera.ContextStorage;
import hera.EmptyContext;
import hera.Invocation;
import hera.Requester;
import hera.api.model.Account;
import hera.api.model.AccountFactory;
import hera.api.model.BytesValue;
import hera.api.model.ContractInterface;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.model.Event;
import hera.api.model.EventFilter;
import hera.api.model.StreamObserver;
import hera.api.model.Subscription;
import hera.key.AergoKeyGenerator;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

public class ContractTemplateTest extends AbstractTestCase {

  protected final ContextStorage<Context> contextStorage = new UnmodifiableContextStorage(
      EmptyContext.getInstance());

  @Test
  public void testGetContractTxReceipt() throws Exception {
    // given
    final ContractTemplate contractTemplate = new ContractTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final ContractTxReceipt expected = ContractTxReceipt.newBuilder().build();
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any()))
        .thenReturn(expected);
    contractTemplate.requester = mockRequester;

    // then
    assertEquals(expected, contractTemplate.getContractTxReceipt(anyTxHash));
    assertEquals(expected, contractTemplate.getReceipt(anyContractTxHash));
  }

  @Test
  public void testDeployTx() throws Exception {
    // given
    final ContractTemplate contractTemplate = new ContractTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final ContractTxHash expected = ContractTxHash.of(BytesValue.EMPTY);
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any()))
        .thenReturn(expected);
    contractTemplate.requester = mockRequester;

    // then
    final Account deprecatedOne = new AccountFactory().create(new AergoKeyGenerator().create());
    assertEquals(expected, contractTemplate.deploy(deprecatedOne, anyDefinition, anyNonce));
    assertEquals(expected, contractTemplate.deploy(deprecatedOne, anyDefinition, anyNonce, anyFee));
    assertEquals(expected, contractTemplate.deploy(anySigner, anyDefinition, anyNonce, anyFee));
    assertEquals(expected, contractTemplate.deployTx(anySigner, anyDefinition, anyNonce, anyFee));
  }

  @Test
  public void testRedeployTx() throws Exception {
    // given
    final ContractTemplate contractTemplate = new ContractTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final ContractTxHash expected = ContractTxHash.of(BytesValue.EMPTY);
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any()))
        .thenReturn(expected);
    contractTemplate.requester = mockRequester;

    // then
    assertEquals(expected, contractTemplate
        .redeploy(anySigner, anyContractAddress, anyDefinition, anyNonce, anyFee));
    assertEquals(expected, contractTemplate
        .redeployTx(anySigner, anyContractAddress, anyDefinition, anyNonce, anyFee));
  }

  @Test
  public void testGetContractInterface() throws Exception {
    // given
    final ContractTemplate contractTemplate = new ContractTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final ContractInterface expected = mock(ContractInterface.class);
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any()))
        .thenReturn(expected);
    contractTemplate.requester = mockRequester;

    // then
    final ContractInterface actual = contractTemplate.getContractInterface(anyContractAddress);
    assertEquals(expected, actual);
  }

  @Test
  public void testExecuteTx() throws Exception {
    // given
    final ContractTemplate contractTemplate = new ContractTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final ContractTxHash expected = ContractTxHash.of(BytesValue.EMPTY);
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any()))
        .thenReturn(expected);
    contractTemplate.requester = mockRequester;

    // then
    final Account deprecatedOne = new AccountFactory().create(new AergoKeyGenerator().create());
    assertEquals(expected, contractTemplate.execute(deprecatedOne, anyInvocation, anyNonce));
    assertEquals(expected,
        contractTemplate.execute(deprecatedOne, anyInvocation, anyNonce, anyFee));
    assertEquals(expected, contractTemplate.execute(anySigner, anyInvocation, anyNonce, anyFee));
    assertEquals(expected, contractTemplate.executeTx(anySigner, anyInvocation, anyNonce, anyFee));
  }

  @Test
  public void testQuery() throws Exception {
    // given
    final ContractTemplate contractTemplate = new ContractTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final ContractResult expected = ContractResult.EMPTY;
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any()))
        .thenReturn(expected);
    contractTemplate.requester = mockRequester;

    // then
    final ContractResult actual = contractTemplate.query(anyInvocation);
    assertEquals(expected, actual);
  }

  @Test
  public void testListEvents() throws Exception {
    // given
    final ContractTemplate contractTemplate = new ContractTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final List<Event> expected = emptyList();
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any()))
        .thenReturn(expected);
    contractTemplate.requester = mockRequester;

    // then
    final EventFilter eventFilter = EventFilter.newBuilder(anyContractAddress).build();
    final List<Event> actual = contractTemplate.listEvents(eventFilter);
    assertEquals(expected, actual);
  }

  @Test
  public void testSubscribeEvent() throws Exception {
    // given
    final ContractTemplate contractTemplate = new ContractTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final Subscription<Event> expected = mock(Subscription.class);
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any()))
        .thenReturn(expected);
    contractTemplate.requester = mockRequester;

    // then
    final EventFilter eventFilter = EventFilter.newBuilder(anyContractAddress).build();
    final Subscription<Event> actual = contractTemplate.subscribeEvent(eventFilter,
        new StreamObserver<Event>() {
          @Override
          public void onNext(Event value) {

          }

          @Override
          public void onError(Throwable t) {

          }

          @Override
          public void onCompleted() {

          }
        });
    assertEquals(expected, actual);
  }

}
