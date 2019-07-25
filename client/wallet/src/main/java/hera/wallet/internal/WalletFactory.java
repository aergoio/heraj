/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet.internal;

import hera.api.model.internal.TryCountAndInterval;
import hera.exception.WalletException;
import hera.keystore.InMemoryKeyStore;
import hera.keystore.JavaKeyStore;
import hera.keystore.ServerKeyStore;
import hera.wallet.WalletApi;
import hera.wallet.WalletType;
import lombok.NonNull;
import lombok.Setter;

/**
 * Factory for Wallet implementation v2. Publicly available on further version.
 *
 * @author taeiklim
 *
 */
public class WalletFactory {

  @Setter
  @NonNull
  protected TryCountAndInterval tryCountAndInterval;

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
