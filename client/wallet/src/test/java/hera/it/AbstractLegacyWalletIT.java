/*
 * @copyright defined in LICENSE.txt
 */

package hera.it;

import static org.junit.Assert.assertEquals;

import hera.api.model.AccountState;
import hera.wallet.Wallet;
import hera.wallet.WalletBuilder;
import hera.wallet.WalletType;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public abstract class AbstractLegacyWalletIT extends AbstractIT {

  protected static final String AERGO_PROPERTIES = "aergo.properties";
  protected static final String SERVER_CRT = "server.crt";
  protected static final String CLIENT_CRT = "client.crt";
  protected static final String CLIENT_KEY = "client.pem";

  protected static Properties properties;

  static {
    try {
      properties = new Properties();
      try (final InputStream in =
          AbstractLegacyWalletIT.class.getResourceAsStream(AERGO_PROPERTIES)) {
        properties.load(in);
      }
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  @Parameters
  public static Collection<Object[]> data() {
    try {
      final List<Object[]> args = new ArrayList<>();

      final Wallet naiveWallet = supplyWallet(WalletType.Naive);
      args.add(new Object[] {naiveWallet});

      final Wallet secureWallet = supplyWallet(WalletType.Secure);
      final java.security.KeyStore keyStore = java.security.KeyStore.getInstance("PKCS12");
      keyStore.load(null, null);
      secureWallet.bindKeyStore(keyStore);
      args.add(new Object[] {secureWallet});

      return args;
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  protected static Wallet supplyWallet(final WalletType type) {
    final String hostname = properties.getProperty("endpoint");
    Wallet wallet = new WalletBuilder()
        .withEndpoint(hostname)
        .withRefresh(2, 1000L, TimeUnit.MILLISECONDS)
        .withNonBlockingConnect()
        .build(type);
    try {
      wallet.getBlockchainStatus();
    } catch (Exception e) {
      final String aergoNodeName = properties.getProperty("aergoNodeName");
      final InputStream serverCert = AbstractLegacyWalletIT.class.getResourceAsStream(SERVER_CRT);
      final InputStream clientCert = AbstractLegacyWalletIT.class.getResourceAsStream(CLIENT_CRT);
      final InputStream clientKey = AbstractLegacyWalletIT.class.getResourceAsStream(CLIENT_KEY);
      wallet = new WalletBuilder()
          .withEndpoint(hostname)
          .withRefresh(2, 1000L, TimeUnit.MILLISECONDS)
          .withTransportSecurity(aergoNodeName, serverCert, clientCert, clientKey)
          .build(type);
    }
    return wallet;
  }

  @Parameter(0)
  public Wallet wallet;

  protected void validatePreAndPostState(final AccountState preState, final long preCachedNonce,
      final AccountState postState, final long postCachedNonce, final long requestCount) {
    assertEquals(preState.getNonce(), preCachedNonce);
    assertEquals(postState.getNonce(), postCachedNonce);
    assertEquals(preState.getNonce() + requestCount, postState.getNonce());
    assertEquals(preCachedNonce + requestCount, postCachedNonce);
  }

}
