/*
 * @copyright defined in LICENSE.txt
 */

package hera.transaction;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.AccountAddress;
import java.util.HashMap;
import java.util.Map;

@ApiAudience.Public
@ApiStability.Unstable
public class SimpleNonceProvider implements NonceProvider {

  // TODO : limit capacity, possible of memory leak
  protected final Map<AccountAddress, Long> address2Nonce = new HashMap<AccountAddress, Long>();

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