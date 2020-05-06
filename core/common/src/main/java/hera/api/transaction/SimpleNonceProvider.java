/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.transaction;

import static hera.util.ValidationUtils.assertNotNull;
import static hera.util.ValidationUtils.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.exception.HerajException;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;

@ApiAudience.Public
@ApiStability.Unstable
public class SimpleNonceProvider implements NonceProvider {

  protected final Logger logger = getLogger(getClass());

  protected final LoadingCache<AccountAddress, AtomicLong> cache;

  /**
   * SimpleNonceProvider constructor. Capacity is set as 1000.
   */
  public SimpleNonceProvider() {
    this(1000);
  }

  /**
   * SimpleNonceProvider constructor. If address more than
   *
   * @param capacity a number of account to hold
   */
  public SimpleNonceProvider(final int capacity) {
    assertTrue(capacity > 0, "Capacity must > 0");
    this.cache = CacheBuilder.newBuilder()
        .concurrencyLevel(Runtime.getRuntime().availableProcessors())
        .maximumSize(capacity)
        .build(new CacheLoader<AccountAddress, AtomicLong>() {
          @Override
          public AtomicLong load(final AccountAddress key) throws Exception {
            return new AtomicLong(0);
          }
        });
  }

  @Override
  public void bindNonce(final AccountState accountState) {
    assertNotNull(accountState, "AccountState must not null");
    bindNonce(accountState.getAddress(), accountState.getNonce());
  }

  @Override
  public void bindNonce(final AccountAddress accountAddress, final long nonce) {
    assertNotNull(accountAddress, "AccountAddress must not null");
    assertTrue(nonce >= 0, "Nonce must > 0");
    this.cache.put(accountAddress, new AtomicLong(nonce));
  }

  @Override
  public long incrementAndGetNonce(final AccountAddress accountAddress) {
    assertNotNull(accountAddress, "AccountAddress must not null");
    try {
      return this.cache.get(accountAddress).incrementAndGet();
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public long getLastUsedNonce(final AccountAddress accountAddress) {
    assertNotNull(accountAddress, "AccountAddress must not null");
    try {
      return this.cache.get(accountAddress).get();
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

}
