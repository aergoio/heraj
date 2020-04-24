/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.Context;
import hera.ContextStorage;
import hera.EmptyContext;
import hera.Invocation;
import hera.Requester;
import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.BytesValue;
import hera.api.model.ChainIdHash;
import hera.api.model.Fee;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.model.TxReceipt;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.key.Signer;
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
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any()))
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
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any()))
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
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any()))
        .thenReturn(expected);
    transactionTemplate.requester = mockRequester;

    // then
    final TxHash actual = transactionTemplate.commit(anyTransaction);
    assertEquals(expected, actual);
  }

  @Test
  public void testSendTx() throws Exception {
    // given
    final TransactionTemplate transactionTemplate = new TransactionTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final TxHash expected = TxHash.of(BytesValue.EMPTY);
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any()))
        .thenReturn(expected);
    transactionTemplate.requester = mockRequester;

    // then
    final TxHash actual = transactionTemplate
        .sendTx(anySigner, anyAccountAddress, anyAmount, anyNonce, anyFee);
    assertEquals(expected, actual);
  }

}
