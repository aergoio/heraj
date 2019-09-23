/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.internal.Time;
import hera.api.model.internal.TryCountAndInterval;
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.client.ClientConfiguer;
import hera.keystore.InMemoryKeyStore;
import hera.keystore.KeyStore;
import hera.wallet.internal.LegacyWallet;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * A legacy wallet api builder.
 *
 * @deprecated use {@link WalletFactory} instead.
 *
 */
@ApiAudience.Public
@ApiStability.Unstable
public class WalletBuilder implements ClientConfiguer<WalletBuilder> {

  protected static final TryCountAndInterval MIMINUM_NONCE_REFRESH_COUNT =
      TryCountAndInterval.of(2, Time.of(100L, TimeUnit.MILLISECONDS));

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
  public WalletBuilder withTimeout(final long timeout, final TimeUnit unit) {
    clientBuilder.withTimeout(timeout, unit);
    return this;
  }

  @Override
  public WalletBuilder withRetry(final int count, final long interval, final TimeUnit unit) {
    clientBuilder.withRetry(count, interval, unit);
    return this;
  }

  @Override
  public WalletBuilder withPlainText() {
    clientBuilder.withPlainText();
    return this;
  }

  @Override
  public WalletBuilder withTransportSecurity(final String serverCommonName,
      final String serverCertPath, final String clientCertPath, final String clientKeyPath) {
    clientBuilder.withTransportSecurity(serverCommonName, serverCertPath, clientCertPath,
        clientKeyPath);
    return this;
  }

  @Override
  public WalletBuilder withTransportSecurity(final String serverCommonName,
      final InputStream serverCertInputStream, final InputStream clientCertInputStream,
      final InputStream clientKeyInputStream) {
    clientBuilder.withTransportSecurity(serverCommonName, serverCertInputStream,
        clientCertInputStream, clientKeyInputStream);
    return this;
  }

  /**
   * A nonce (or chain id hash) refresh count to handle invalid case. A minimum is 1.
   *
   * @param count retry count. If it is less than 0, set as 1
   * @param interval interval value. If it's less than 0, set as 1
   * @param unit interval unit
   * @return an instance of this
   */
  public WalletBuilder withRefresh(final int count, final long interval, final TimeUnit unit) {
    this.nonceRefreshTryInterval =
        TryCountAndInterval.of(count <= 0 ? 1 : count, Time.of(interval <= 0 ? 1 : interval, unit));
    return this;
  }

  /**
   * Create a wallet instance.
   *
   * @param walletType a wallet type
   * @return a wallet instance
   */
  public Wallet build(final WalletType walletType) {
    final AergoClient aergoClient = clientBuilder.build();

    if (!walletType.equals(WalletType.Naive)) {
      throw new UnsupportedOperationException();
    }

    final KeyStore keyStore = new InMemoryKeyStore();
    WalletApi delegate = new WalletFactory().create(keyStore, nonceRefreshTryInterval.getCount(),
        nonceRefreshTryInterval.getInterval().toMilliseconds());
    delegate.bind(aergoClient);

    return new LegacyWallet(delegate);
  }

}
