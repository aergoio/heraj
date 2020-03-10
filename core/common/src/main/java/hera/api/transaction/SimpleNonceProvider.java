/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.transaction;

import static hera.util.ValidationUtils.assertNotNull;
import static hera.util.ValidationUtils.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;

@ApiAudience.Public
@ApiStability.Unstable
public class SimpleNonceProvider implements NonceProvider {

  protected final Logger logger = getLogger(getClass());

  protected final Object lock = new Object();

  protected final Map<AccountAddress, Long> address2Nonce;

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
    this.address2Nonce = new LinkedHashMap<AccountAddress, Long>() {

      private static final long serialVersionUID = 7283839934243345689L;

      @Override
      protected boolean removeEldestEntry(java.util.Map.Entry<AccountAddress, Long> eldest) {
        logger.debug("Capacity is over {}. Remove stale entry: {}", capacity, eldest);
        return size() > capacity;
      }

    };
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
    synchronized (lock) {
      address2Nonce.put(accountAddress, nonce);
    }
  }

  @Override
  public long incrementAndGetNonce(final AccountAddress accountAddress) {
    assertNotNull(accountAddress, "AccountAddress must not null");
    synchronized (lock) {
      final Long lastNonce = address2Nonce.get(accountAddress);
      final long nextNonce = null == lastNonce ? 1L : lastNonce + 1;
      address2Nonce.put(accountAddress, nextNonce);
      return nextNonce;
    }
  }

  @Override
  public long getLastUsedNonce(final AccountAddress accountAddress) {
    assertNotNull(accountAddress, "AccountAddress must not null");
    synchronized (lock) {
      final Long lastNonce = address2Nonce.get(accountAddress);
      return null == lastNonce ? 0L : lastNonce;
    }
  }

}
