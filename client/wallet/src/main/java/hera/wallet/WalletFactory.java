/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.internal.Time;
import hera.api.model.internal.TryCountAndInterval;
import hera.exception.WalletException;
import hera.keystore.InMemoryKeyStore;
import hera.keystore.JavaKeyStore;
import hera.keystore.ServerKeyStore;
import hera.wallet.internal.WalletApiImpl;
import java.util.concurrent.TimeUnit;

/**
 * Factory for Wallet implementation v2. This is beta version
 *
 * @author taeiklim
 *
 */
@ApiAudience.Public
@ApiStability.Unstable
public class WalletFactory {

  protected TryCountAndInterval tryCountAndInterval =
      TryCountAndInterval.of(3, Time.of(0, TimeUnit.SECONDS));

  public void setRefresh(final int count, final long interval, final TimeUnit unit) {
    this.tryCountAndInterval = new TryCountAndInterval(count, Time.of(interval, unit));
  }

  /**
   * Create a wallet instance.
   *
   * @param walletType a wallet type
   *
   * @return a wallet instance
   */
  public WalletApi create(final WalletType walletType) {
    switch (walletType) {
      case Naive:
        return new WalletApiImpl(tryCountAndInterval, new InMemoryKeyStore());
      case Secure:
        return new WalletApiImpl(tryCountAndInterval, new JavaKeyStore());
      case ServerKeyStore:
        return new WalletApiImpl(tryCountAndInterval, new ServerKeyStore());
      default:
        throw new WalletException("Invalid wallet type " + walletType);
    }

  }

}
