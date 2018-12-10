/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import hera.client.AergoClientBuilder;
import hera.client.ContextConfiguer;
import hera.exception.WalletCreationException;
import hera.key.AergoKey;
import java.util.concurrent.TimeUnit;

public class WalletFactory implements ContextConfiguer<WalletFactory> {

  protected static final int MIMINUM_NONCE_REFRESH_COUNT = 1;

  protected AergoClientBuilder clientBuilder = new AergoClientBuilder();

  protected int nonceRefreshCount = MIMINUM_NONCE_REFRESH_COUNT;

  @Override
  public WalletFactory addConfiguration(final String key, final String value) {
    clientBuilder.addConfiguration(key, value);
    return this;
  }

  @Override
  public WalletFactory withEndpoint(final String endpoint) {
    clientBuilder.withEndpoint(endpoint);
    return this;
  }

  @Override
  public WalletFactory withNonBlockingConnect() {
    clientBuilder.withNonBlockingConnect();
    return this;
  }

  @Override
  public WalletFactory withBlockingConnect() {
    clientBuilder.withBlockingConnect();
    return this;
  }

  @Override
  public WalletFactory withTracking() {
    clientBuilder.withTracking();
    return this;
  }

  @Override
  public WalletFactory withTimeout(final long timeout, final TimeUnit unit) {
    clientBuilder.withTimeout(timeout, unit);
    return this;
  }

  @Override
  public WalletFactory withRetry(final int count, final long interval, final TimeUnit unit) {
    clientBuilder.withRetry(count, interval, unit);
    return this;
  }

  /**
   * A nonce refresh count to handle invalid nonce. A minimum is 1.
   *
   * @param nonceRefreshCount nonce refresh count
   * @return an instance of this
   */
  public WalletFactory withNonceRefresh(final int nonceRefreshCount) {
    this.nonceRefreshCount = MIMINUM_NONCE_REFRESH_COUNT < nonceRefreshCount ? nonceRefreshCount
        : MIMINUM_NONCE_REFRESH_COUNT;
    return this;
  }

  /**
   * Create a wallet instance with the new key.
   *
   * @param type a wallet type
   * @return a wallet instance
   * @throws WalletCreationException if wallet type is invalid
   */
  public Wallet create(final WalletType type) {
    if (null == type) {
      throw new WalletCreationException("Unrecognized wallet type");
    }
    switch (type) {
      case Naive:
        return new NaiveWallet(clientBuilder.build(), nonceRefreshCount);
      case Secure:
        throw new UnsupportedOperationException("Not yet implemented");
      case ServerKeyStore:
        throw new UnsupportedOperationException("Not yet implemented");
      default:
        throw new WalletCreationException("Unrecognized wallet type");
    }
  }

  /**
   * Create a wallet instance with a initial key.
   *
   * @param type a wallet type
   * @param key an aergo key
   * @return a wallet instance
   * @throws WalletCreationException if wallet type is invalid
   */
  public Wallet create(final WalletType type, final AergoKey key) {
    if (null == type) {
      throw new WalletCreationException("Unrecognized wallet type");
    }
    switch (type) {
      case Naive:
        return new NaiveWallet(clientBuilder.build(), nonceRefreshCount, key);
      case Secure:
        throw new UnsupportedOperationException("Not yet implemented");
      case ServerKeyStore:
        throw new UnsupportedOperationException("Not yet implemented");
      default:
        throw new WalletCreationException("Unrecognized wallet type");
    }
  }

}
