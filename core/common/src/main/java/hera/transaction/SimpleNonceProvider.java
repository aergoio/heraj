/*
 * @copyright defined in LICENSE.txt
 */

package hera.transaction;

import static hera.util.ValidationUtils.assertTrue;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.AccountAddress;
import java.util.LinkedHashMap;
import java.util.Map;

@ApiAudience.Public
@ApiStability.Unstable
public class SimpleNonceProvider implements NonceProvider {

  protected final Map<AccountAddress, Long> address2Nonce;

  /**
   * SimpleNonceProvider constructor. Capacity is set as 100.
   */
  public SimpleNonceProvider() {
    this(100);
  }

  /**
   * SimpleNonceProvider constructor.
   *
   * @param capacity a number of account to hold
   */
  public SimpleNonceProvider(final int capacity) {
    assertTrue(capacity > 0, "Capacity must > 0");
    this.address2Nonce = new LinkedHashMap<AccountAddress, Long>() {

      private static final long serialVersionUID = 7283839934243345689L;

      @Override
      protected boolean removeEldestEntry(java.util.Map.Entry<AccountAddress, Long> eldest) {
        return size() > capacity;
      }

    };
  }

  @Override
  public synchronized void bindNonce(final AccountAddress accountAddress, final long nonce) {
    address2Nonce.put(accountAddress, nonce);
  }

  @Override
  public synchronized long incrementAndGetNonce(final AccountAddress accountAddress) {
    final Long lastNonce = address2Nonce.get(accountAddress);
    long nextNonce = null == lastNonce ? 1L : lastNonce + 1;
    address2Nonce.put(accountAddress, nextNonce);
    return nextNonce;
  }

}
