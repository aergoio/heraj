/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import hera.AbstractTestCase;
import hera.api.AccountOperation;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.BytesValue;
import hera.api.model.Time;
import hera.api.model.TryCountAndInterval;
import hera.api.model.TxHash;
import hera.api.transaction.SimpleNonceProvider;
import hera.exception.CommitException;
import hera.exception.ConnectionException;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.key.Signer;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import types.Rpc.CommitStatus;

public class NonceRefreshingTxRequesterTest extends AbstractTestCase {

  @Test
  public void testRequest() throws Exception {
    final TryCountAndInterval tryCountAndInterval = TryCountAndInterval.of(3, Time.of(100L));
    final TxRequester txRequester = new NonceRefreshingTxRequester(tryCountAndInterval,
        new SimpleNonceProvider());
    final AergoKey signer = new AergoKeyGenerator().create();
    final TxHash expected = TxHash.of(BytesValue.of(randomUUID().toString().getBytes()));
    final TxHash actual = txRequester
        .request(mock(AergoClient.class), signer, new TxRequestFunction() {
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
    final AergoKey signer = new AergoKeyGenerator().create();
    final AccountAddress address = signer.getPrincipal();
    final long initialNonce = 2;
    final AccountOperation mockAccountOperation = mock(AccountOperation.class);
    when(mockAccountOperation.getState(eq(address)))
        .thenReturn(AccountState.newBuilder().address(address).nonce(initialNonce).build());
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getAccountOperation()).thenReturn(mockAccountOperation);

    // then
    final int count = 3;
    final AtomicInteger countDown = new AtomicInteger(count);
    final TryCountAndInterval tryCountAndInterval = TryCountAndInterval.of(count, Time.of(100L));
    final SimpleNonceProvider nonceProvider = new SimpleNonceProvider();
    final NonceRefreshingTxRequester txRequester = new NonceRefreshingTxRequester(tryCountAndInterval,
            nonceProvider);
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
    long prevNonce = nonceProvider.getLastUsedNonce(signer.getPrincipal());
    final TxHash actual = txRequester.request(mockClient, signer, requestFunction);
    assertEquals(expected, actual);
    assertEquals(initialNonce+1, nonceProvider.getLastUsedNonce(signer.getAddress()));
  }

  @Test
  public void shouldRequestFailEvenOnNonceRefresh() throws Exception {
    // given
    final AergoKey signer = new AergoKeyGenerator().create();
    final AccountAddress address = signer.getPrincipal();
    final long initialNonce = 2;
    final AccountOperation mockAccountOperation = mock(AccountOperation.class);
    when(mockAccountOperation.getState(eq(address)))
            .thenReturn(AccountState.newBuilder().address(address).nonce(initialNonce).build());
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getAccountOperation()).thenReturn(mockAccountOperation);
    final TryCountAndInterval tryCountAndInterval = TryCountAndInterval.of(3, Time.of(100L));
    final SimpleNonceProvider nonceProvider = new SimpleNonceProvider();
    final TxRequester txRequester = new NonceRefreshingTxRequester(tryCountAndInterval,
            nonceProvider);
    final CommitException expected = new CommitException(CommitStatus.TX_HAS_SAME_NONCE, "Error");
    final TxRequestFunction requestFunction = new TxRequestFunction() {
      @Override
      public TxHash apply(Signer signer, Long aLong) {
        throw expected;
      }
    };
    long prevNonce = nonceProvider.getLastUsedNonce(signer.getAddress());
    try {
      txRequester.request(mockClient, signer, requestFunction);
      fail("Should throw CommitException");
    } catch (Exception actual) {
      // then
      assertEquals(expected, actual);
      // nonce should not be changed if request was failed.
      assertEquals(initialNonce, nonceProvider.getLastUsedNonce(signer.getAddress()));
    }
  }

  @Test
  public void shouldRequestFailOnNotNonceRelatedError() {
    // given
    final AergoKey signer = new AergoKeyGenerator().create();
    final AccountAddress address = signer.getPrincipal();
    final long initialNonce = 2;
    final AccountOperation mockAccountOperation = mock(AccountOperation.class);
    when(mockAccountOperation.getState(eq(address)))
            .thenReturn(AccountState.newBuilder().address(address).nonce(initialNonce).build());
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getAccountOperation()).thenReturn(mockAccountOperation);
    final TryCountAndInterval tryCountAndInterval = TryCountAndInterval.of(3, Time.of(100L));
    final UnsupportedOperationException expected = new UnsupportedOperationException(
        "Not nonce-related error");
    final TxRequestFunction requestFunction = new TxRequestFunction() {
      @Override
      public TxHash apply(Signer signer, Long aLong) {
        throw expected;
      }
    };
    final SimpleNonceProvider nonceProvider = new SimpleNonceProvider();
    nonceProvider.bindNonce(signer.getAddress(), initialNonce);
    final TxRequester txRequester = new NonceRefreshingTxRequester(tryCountAndInterval,
            nonceProvider);

    long prevNonce = nonceProvider.getLastUsedNonce(signer.getAddress());
    try {
      txRequester.request(mockClient, signer, requestFunction);
      fail("Should throw UnsupportedOperationException");
    } catch (Exception actual) {
      // then
      assertEquals(expected, actual);
      // nonce should not be changed if request was failed.
      assertEquals(prevNonce, nonceProvider.getLastUsedNonce(signer.getAddress()));
    }
    verify(mockAccountOperation, times(1)).getState(any(AccountAddress.class));
  }

  @Test
  public void requestFailAndFailToSyncNonce() {
    // given
    final AergoKey signer = new AergoKeyGenerator().create();
    final AccountAddress address = signer.getPrincipal();
    final long initialNonce = 2;
    final AccountOperation mockAccountOperation = mock(AccountOperation.class);
    when(mockAccountOperation.getState(eq(address)))
            .thenThrow(new ConnectionException("connectino unavailable"));
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getAccountOperation()).thenReturn(mockAccountOperation);
    final TryCountAndInterval tryCountAndInterval = TryCountAndInterval.of(3, Time.of(100L));
    final ConnectionException expected = new ConnectionException("rpc server unavailable");
    final TxRequestFunction requestFunction = new TxRequestFunction() {
      @Override
      public TxHash apply(Signer signer, Long aLong) {
        throw expected;
      }
    };
    final SimpleNonceProvider nonceProvider = new SimpleNonceProvider();
    nonceProvider.bindNonce(signer.getAddress(), initialNonce);
    final TxRequester txRequester = new NonceRefreshingTxRequester(tryCountAndInterval,
            nonceProvider);

    long prevNonce = nonceProvider.getLastUsedNonce(signer.getAddress());
    try {
      txRequester.request(mockClient, signer, requestFunction);
      fail("Should throw UnsupportedOperationException");
    } catch (Exception actual) {
      // then
      assertEquals(expected, actual);
      // nonce should not be changed if request was failed.
      assertEquals(0L, nonceProvider.getLastUsedNonce(signer.getAddress()));
    }
    verify(mockAccountOperation, times(1)).getState(any(AccountAddress.class));
  }

}
