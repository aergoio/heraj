/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import static hera.util.ValidationUtils.assertNotNull;
import static hera.util.ValidationUtils.assertTrue;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Time;
import hera.api.model.internal.TryCountAndInterval;

@ApiAudience.Public
@ApiStability.Unstable
public class WalletApiFactory {

  public static final int DEFAULT_RETRY_COUNT = 2;

  public static final long DEFAULT_RETRY_INTERVAL = 100L;

  /**
   * Create a wallet instance with retryCont as {@value #DEFAULT_RETRY_COUNT} and retry interval as
   * {@value #DEFAULT_RETRY_INTERVAL} milliseconds.
   *
   * @param keyStore an keystore instance
   * @return a wallet instance
   */
  public WalletApi create(final hera.keystore.KeyStore keyStore) {
    return create(keyStore, DEFAULT_RETRY_COUNT, DEFAULT_RETRY_INTERVAL);
  }

  /**
   * Create a wallet instance.
   *
   * @param keyStore      an keystore instance
   * @param retryCount    a retry count on nonce failure
   * @param retryInterval a retry interval in milliseconds on nonce failure
   * @return a wallet instance
   */
  public WalletApi create(final hera.keystore.KeyStore keyStore, final int retryCount,
      final long retryInterval) {
    assertNotNull(keyStore);
    assertTrue(1 <= retryCount);
    assertTrue(0 < retryInterval);
    return new WalletApiImpl(keyStore, new TryCountAndInterval(retryCount, Time.of(retryInterval)));
  }

}
