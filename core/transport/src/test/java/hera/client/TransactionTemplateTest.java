/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.Context;
import hera.ContextStorage;
import hera.EmptyContext;
import hera.Invocation;
import hera.Requester;
import hera.api.model.BytesValue;
import hera.api.model.Name;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.model.TxReceipt;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

public class TransactionTemplateTest extends AbstractTestCase {

  protected final ContextStorage<Context> contextStorage = new UnmodifiableContextStorage(
      EmptyContext.getInstance());

  @Test
  public void testGetTransaction() throws Exception {
    // given
    final TransactionTemplate transactionTemplate = new TransactionTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final Transaction expected = anyTransaction;
    when(mockRequester.request(ArgumentMatchers.<Invocation<Transaction>>any()))
        .thenReturn(expected);
    transactionTemplate.requester = mockRequester;

    // then
    final Transaction actual = transactionTemplate.getTransaction(anyTxHash);
    assertEquals(expected, actual);
  }

  @Test
  public void testGetTxReceipt() throws Exception {
    // given
    final TransactionTemplate transactionTemplate = new TransactionTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final TxReceipt expected = TxReceipt.newBuilder().build();
    when(mockRequester.request(ArgumentMatchers.<Invocation<TxReceipt>>any()))
        .thenReturn(expected);
    transactionTemplate.requester = mockRequester;

    // then
    final TxReceipt actual = transactionTemplate.getTxReceipt(anyTxHash);
    assertEquals(expected, actual);
  }

  @Test
  public void testCommit() throws Exception {
    // given
    final TransactionTemplate transactionTemplate = new TransactionTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final TxHash expected = TxHash.of(BytesValue.EMPTY);
    when(mockRequester.request(ArgumentMatchers.<Invocation<TxHash>>any()))
        .thenReturn(expected);
    transactionTemplate.requester = mockRequester;

    // then
    final TxHash actual = transactionTemplate.commit(anyTransaction);
    assertEquals(expected, actual);
  }

  @Test
  public void testSendTxByAddress() throws Exception {
    // given
    final TransactionTemplate transactionTemplate = new TransactionTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final TxHash expected = TxHash.of(BytesValue.EMPTY);
    when(mockRequester.request(ArgumentMatchers.<Invocation<TxHash>>any()))
        .thenReturn(expected);
    transactionTemplate.requester = mockRequester;

    // then
    final TxHash actual = transactionTemplate
        .sendTx(anySigner, anyAccountAddress, anyAmount, anyNonce, anyFee, anyPayload);
    assertEquals(expected, actual);
  }

  @Test
  public void testSendTxByName() throws Exception {
    // given
    final TransactionTemplate transactionTemplate = new TransactionTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final TxHash expected = TxHash.of(BytesValue.EMPTY);
    when(mockRequester.request(ArgumentMatchers.<Invocation<TxHash>>any()))
        .thenReturn(expected);
    transactionTemplate.requester = mockRequester;

    // then
    final TxHash actual = transactionTemplate
        .sendTx(anySigner, anyName, anyAmount, anyNonce, anyFee, anyPayload);
    assertEquals(expected, actual);
  }

}
