/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import hera.api.model.Time;
import hera.api.model.internal.TryCountAndInterval;
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.client.ContextConfiguer;
import hera.exception.WalletCreationException;
import java.util.concurrent.TimeUnit;

public class WalletBuilder implements ContextConfiguer<WalletBuilder> {

  protected static final TryCountAndInterval MIMINUM_NONCE_REFRESH_COUNT =
      TryCountAndInterval.of(1, Time.of(0, TimeUnit.SECONDS));

  protected AergoClientBuilder clientBuilder = new AergoClientBuilder();

  protected TryCountAndInterval nonceRefreshTryInterval = MIMINUM_NONCE_REFRESH_COUNT;

  @Override
  public WalletBuilder addConfiguration(final String key, final String value) {
    clientBuilder.addConfiguration(key, value);
    return this;
  }

  @Override
  public WalletBuilder withEndpoint(final String endpoint) {
    clientBuilder.withEndpoint(endpoint);
    return this;
  }

  @Override
  public WalletBuilder withNonBlockingConnect() {
    clientBuilder.withNonBlockingConnect();
    return this;
  }

  @Override
  public WalletBuilder withBlockingConnect() {
    clientBuilder.withBlockingConnect();
    return this;
  }

  @Override
  public WalletBuilder withTracking() {
    clientBuilder.withTracking();
    return this;
  }

  @Override
  public WalletBuilder withTimeout(final long timeout, final TimeUnit unit) {
    clientBuilder.withTimeout(timeout, unit);
    return this;
  }

  @Override
  public WalletBuilder withRetry(final int count, final long interval, final TimeUnit unit) {
    clientBuilder.withRetry(count, interval, unit);
    return this;
  }

  /**
   * A nonce refresh count to handle invalid nonce. A minimum is 1.
   *
   * @param count retry count. If it is less than 0, set as 1
   * @param interval interval value. If it's less than 0, set as 1
   * @param unit interval unit
   * @return an instance of this
   */
  public WalletBuilder withNonceRefresh(final int count, final long interval, final TimeUnit unit) {
    this.nonceRefreshTryInterval =
        TryCountAndInterval.of(count <= 0 ? 1 : count, Time.of(interval <= 0 ? 1 : interval, unit));
    return this;
  }

  /**
   * Create a wallet instance.
   *
   * @param type a wallet type
   * @return a wallet instance
   * @throws WalletCreationException if wallet type is invalid
   */
  public Wallet build(final WalletType type) {
    if (null == type) {
      throw new WalletCreationException("Unrecognized wallet type");
    }
    final AergoClient aergoClient = clientBuilder.build();
    switch (type) {
      case Naive:
        return new NaiveWallet(aergoClient, nonceRefreshTryInterval);
      case Secure:
        throw new UnsupportedOperationException("Not yet implemented");
      case ServerKeyStore:
        return new ServerKeyStoreWallet(aergoClient, nonceRefreshTryInterval);
      default:
        throw new WalletCreationException("Unrecognized wallet type");
    }
  }

}
