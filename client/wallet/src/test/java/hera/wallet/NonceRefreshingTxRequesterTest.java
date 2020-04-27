/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.api.AccountOperation;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.BytesValue;
import hera.api.model.Time;
import hera.api.model.TxHash;
import hera.client.AergoClient;
import hera.exception.CommitException;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.key.Signer;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import types.Rpc.CommitStatus;

public class NonceRefreshingTxRequesterTest extends AbstractTestCase {

  @Test
  public void testRequest() throws Exception {
    final ClientProvider clientProvider = mock(ClientProvider.class);
    final TryCountAndInterval tryCountAndInterval = TryCountAndInterval.of(3, Time.of(100L));
    final TxRequester txRequester = new NonceRefreshingTxRequester(clientProvider,
        tryCountAndInterval);
    final AergoKey signer = new AergoKeyGenerator().create();
    final TxHash expected = TxHash.of(BytesValue.of(randomUUID().toString().getBytes()));
    final TxHash actual = txRequester.request(signer, new TxRequestFunction() {
      @Override
      public TxHash apply(Signer signer, Long aLong) {
        return expected;
      }
    });
    assertEquals(expected, actual);
  }

  @Test
  public void shouldRequestSuccessOnNonceRefresh() throws Exception {
    // given
    final AccountOperation mockAccountOperation = mock(AccountOperation.class);
    when(mockAccountOperation.getState(any(AccountAddress.class)))
        .thenReturn(AccountState.newBuilder().build());
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getAccountOperation()).thenReturn(mockAccountOperation);
    final ClientProvider clientProvider = mock(ClientProvider.class);
    when(clientProvider.getClient()).thenReturn(mockClient);

    // then
    final int count = 3;
    final AtomicInteger countDown = new AtomicInteger(count);
    final TryCountAndInterval tryCountAndInterval = TryCountAndInterval.of(count, Time.of(100L));
    final TxRequester txRequester = new NonceRefreshingTxRequester(clientProvider,
        tryCountAndInterval);
    final AergoKey signer = new AergoKeyGenerator().create();
    final TxHash expected = TxHash.of(BytesValue.of(randomUUID().toString().getBytes()));
    final TxRequestFunction requestFunction = new TxRequestFunction() {
      @Override
      public TxHash apply(Signer signer, Long aLong) {
        if (countDown.get() <= 1) {
          return expected;
        }
        countDown.decrementAndGet();
        throw new CommitException(types.Rpc.CommitStatus.TX_HAS_SAME_NONCE, "");
      }
    };
    final TxHash actual = txRequester.request(signer, requestFunction);
    assertEquals(expected, actual);
  }

  @Test
  public void shouldRequestFailEvenOnNonceRefresh() throws Exception {
    // given
    final AccountOperation mockAccountOperation = mock(AccountOperation.class);
    when(mockAccountOperation.getState(any(AccountAddress.class)))
        .thenReturn(AccountState.newBuilder().build());
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getAccountOperation()).thenReturn(mockAccountOperation);
    final ClientProvider clientProvider = mock(ClientProvider.class);
    when(clientProvider.getClient()).thenReturn(mockClient);
    final TryCountAndInterval tryCountAndInterval = TryCountAndInterval.of(3, Time.of(100L));
    final TxRequester txRequester = new NonceRefreshingTxRequester(clientProvider,
        tryCountAndInterval);
    final AergoKey signer = new AergoKeyGenerator().create();
    final CommitException expected = new CommitException(CommitStatus.TX_HAS_SAME_NONCE, "Error");
    final TxRequestFunction requestFunction = new TxRequestFunction() {
      @Override
      public TxHash apply(Signer signer, Long aLong) {
        throw expected;
      }
    };

    try {
      txRequester.request(signer, requestFunction);
      fail("Should throw CommitException");
    } catch (Exception actual) {
      // then
      assertEquals(expected, actual);
    }
  }

  @Test
  public void shouldRequestFailOnNotNonceRelatedError() {
    // given
    final ClientProvider clientProvider = mock(ClientProvider.class);
    final TryCountAndInterval tryCountAndInterval = TryCountAndInterval.of(3, Time.of(100L));
    final TxRequester txRequester = new NonceRefreshingTxRequester(clientProvider,
        tryCountAndInterval);
    final AergoKey signer = new AergoKeyGenerator().create();
    final UnsupportedOperationException expected = new UnsupportedOperationException(
        "Not nonce-related error");
    final TxRequestFunction requestFunction = new TxRequestFunction() {
      @Override
      public TxHash apply(Signer signer, Long aLong) {
        throw expected;
      }
    };

    try {
      txRequester.request(signer, requestFunction);
      fail("Should throw UnsupportedOperationException");
    } catch (Exception actual) {
      // then
      assertEquals(expected, actual);
    }
  }

}
