/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

/**
 * Use {@link WalletApiFactory} instead.
 */
@Deprecated
public class WalletFactory {

  protected final WalletApiFactory delegate = new WalletApiFactory();

  /**
   * See {@link WalletApiFactory#create(hera.keystore.KeyStore)}.
   *
   * @param keyStore an keystore instance
   *
   * @return a wallet instance
   */
  public WalletApi create(final hera.keystore.KeyStore keyStore) {
    return delegate.create(keyStore);
  }

  /**
   * See {@link WalletApiFactory#create(hera.keystore.KeyStore, int, long)}.
   *
   * @param keyStore an keystore instance
   * @param retryCount a retry count on nonce failure
   * @param retryInterval a retry interval in milliseconds on nonce failure
   * @return a wallet instance
   */
  public WalletApi create(final hera.keystore.KeyStore keyStore, final int retryCount,
      final long retryInterval) {
    return delegate.create(keyStore, retryCount, retryInterval);
  }

}
