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
import hera.WriteSynchronizedContextStorage;
import hera.api.model.BytesValue;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
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
import hera.key.Signer;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

public class ContractTemplateTest extends AbstractTestCase {

  protected final ContextStorage<Context> contextStorage = new WriteSynchronizedContextStorage<>();

  {
    contextStorage.put(EmptyContext.getInstance());
  }

  @Test
  public void testGetReceipt() throws Exception {
    // given
    final ContractTemplate contractTemplate = new ContractTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final ContractTxReceipt expected = ContractTxReceipt.newBuilder().build();
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any()))
        .thenReturn(expected);
    contractTemplate.requester = mockRequester;

    // then
    final ContractTxReceipt actual = contractTemplate
        .getReceipt(ContractTxHash.of(BytesValue.EMPTY));
    assertEquals(expected, actual);
  }

  @Test
  public void testDeploy() throws Exception {
    // given
    final ContractTemplate contractTemplate = new ContractTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final ContractTxHash expected = ContractTxHash.of(BytesValue.EMPTY);
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any()))
        .thenReturn(expected);
    contractTemplate.requester = mockRequester;

    // then
    final Signer signer = new AergoKeyGenerator().create();
    final ContractTxHash actual = contractTemplate
        .deploy(signer, mock(ContractDefinition.class), 1L, Fee.ZERO);
    assertEquals(expected, actual);
  }

  @Test
  public void testRedeploy() throws Exception {
    // given
    final ContractTemplate contractTemplate = new ContractTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final ContractTxHash expected = ContractTxHash.of(BytesValue.EMPTY);
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any()))
        .thenReturn(expected);
    contractTemplate.requester = mockRequester;

    // then
    final Signer signer = new AergoKeyGenerator().create();
    final ContractTxHash actual = contractTemplate
        .redeploy(signer, ContractAddress.EMPTY, mock(ContractDefinition.class), 1L, Fee.ZERO);
    assertEquals(expected, actual);
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
    final ContractInterface actual = contractTemplate.getContractInterface(ContractAddress.EMPTY);
    assertEquals(expected, actual);
  }

  @Test
  public void testExecute() throws Exception {
    // given
    final ContractTemplate contractTemplate = new ContractTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final ContractTxHash expected = ContractTxHash.of(BytesValue.EMPTY);
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any()))
        .thenReturn(expected);
    contractTemplate.requester = mockRequester;

    // then
    final Signer signer = new AergoKeyGenerator().create();
    final ContractTxHash actual = contractTemplate
        .execute(signer, mock(ContractInvocation.class), 1L, Fee.ZERO);
    assertEquals(expected, actual);
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
    final ContractResult actual = contractTemplate.query(mock(ContractInvocation.class));
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
    final EventFilter eventFilter = EventFilter.newBuilder(ContractAddress.EMPTY).build();
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
    final EventFilter eventFilter = EventFilter.newBuilder(ContractAddress.EMPTY).build();
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


