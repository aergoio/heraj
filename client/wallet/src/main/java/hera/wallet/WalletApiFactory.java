/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import static hera.util.ValidationUtils.assertNotNull;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Time;
import hera.api.model.TryCountAndInterval;

@ApiAudience.Public
@ApiStability.Unstable
public class WalletApiFactory {

  public static final TryCountAndInterval DEFAULT_TRY_COUNT_AND_INTERVAL = TryCountAndInterval
      .of(3, Time.of(100L));

  /**
   * Create a wallet instance with {@link #DEFAULT_TRY_COUNT_AND_INTERVAL}.
   *
   * @param keyStore an keystore instance
   * @return a wallet instance
   */
  public WalletApi create(final hera.keystore.KeyStore keyStore) {
    return create(keyStore, DEFAULT_TRY_COUNT_AND_INTERVAL);
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
    return create(keyStore, TryCountAndInterval.of(retryCount, Time.of(retryInterval)));
  }

  /**
   * Create a wallet instance.
   *
   * @param keyStore            an keystore instance
   * @param tryCountAndInterval a retry count and interval on nonce failure
   * @return a wallet instance
   */
  public WalletApi create(final hera.keystore.KeyStore keyStore,
      final TryCountAndInterval tryCountAndInterval) {
    assertNotNull(keyStore, "Keystore must not null");
    assertNotNull(tryCountAndInterval, "TryCountAndInterval must not null");
    return new WalletApiImpl(keyStore, tryCountAndInterval);
  }

}
