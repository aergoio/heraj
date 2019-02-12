/*
 * * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.function.Function0;
import hera.api.function.Function1;
import hera.api.model.Account;
import hera.api.model.AccountFactory;
import hera.api.model.BytesValue;
import hera.api.model.TxHash;
import hera.api.model.internal.Time;
import hera.api.model.internal.TryCountAndInterval;
import hera.exception.CommitException;
import hera.key.AergoKeyGenerator;
import java.util.concurrent.CountDownLatch;
import org.junit.Before;
import org.junit.Test;

public class TransactionTrierTest extends AbstractTestCase {

  protected final int count = 3;

  protected TransactionTrier trier;

  @Before
  public void setUp() {
    this.trier = new TransactionTrier(TryCountAndInterval.of(count, Time.of(1000L)));
    this.trier.setAccountProvider(new Function0<Account>() {
      @Override
      public Account apply() {
        return new AccountFactory().create(new AergoKeyGenerator().create());
      }
    });
    this.trier.setNonceSynchronizer(new Runnable() {
      @Override
      public void run() {
        // do nothing
      }
    });
  }

  @Test
  public void testRequestWithoutFirstTrier() {
    final TxHash txHash = trier.request(new Function1<Long, TxHash>() {
      protected CountDownLatch latch = new CountDownLatch(count);

      @Override
      public TxHash apply(Long nonce) {
        if (0 == latch.getCount()) {
          return TxHash.of(BytesValue.EMPTY);
        }
        latch.countDown();
        throw new CommitException(types.Rpc.CommitStatus.TX_NONCE_TOO_LOW, "Nonce is too low");
      }
    });
    assertNotNull(txHash);
  }

  @Test
  public void testRequestOnFirstTrySuccess() {
    final TxHash txHash = trier.request(new Function0<TxHash>() {
      @Override
      public TxHash apply() {
        return TxHash.of(BytesValue.EMPTY);
      }
    }, new Function1<Long, TxHash>() {
      @Override
      public TxHash apply(Long nonce) {
        throw new UnsupportedOperationException();
      }
    });
    assertNotNull(txHash);
  }

  @Test
  public void testRequestOnFirstTryFail() {
    final TxHash txHash = trier.request(new Function0<TxHash>() {
      @Override
      public TxHash apply() {
        return null;
      }
    }, new Function1<Long, TxHash>() {
      protected CountDownLatch latch = new CountDownLatch(count);

      @Override
      public TxHash apply(Long nonce) {
        if (0 == latch.getCount()) {
          return TxHash.of(BytesValue.EMPTY);
        }
        latch.countDown();
        throw new CommitException(types.Rpc.CommitStatus.TX_NONCE_TOO_LOW, "Nonce is too low");
      }
    });
    assertNotNull(txHash);
  }

}
