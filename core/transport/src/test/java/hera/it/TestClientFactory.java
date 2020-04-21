/*
 * @copyright defined in LICENSE.txt
 */

package hera.it;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.ChainIdHash;
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;

class TestClientFactory {

  protected static final String AERGO_PROPERTIES = "aergo.properties";
  protected static final String SERVER_CRT = "server.crt";
  protected static final String CLIENT_CRT = "client.crt";
  protected static final String CLIENT_KEY = "client.pem";

  protected final transient Logger logger = getLogger(getClass());
  protected final Properties properties;

  TestClientFactory() {
    try {
      final Properties properties = new Properties();
      try (final InputStream in = getClass().getResourceAsStream(AERGO_PROPERTIES)) {
        properties.load(in);
      }
      this.properties = properties;
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  public AergoClient get() {
    final String hostname = properties.getProperty("endpoint");
    logger.debug("Provide client for {}", hostname);
    final AergoClientBuilder clientBuilder = new AergoClientBuilder()
        .withNonBlockingConnect()
        .withTimeout(3000, TimeUnit.MILLISECONDS)
        .withEndpoint(hostname)
        .withPlainText();
    AergoClient client = clientBuilder.build();
    // try plaintext first
    try {
      client.getBlockchainOperation().getBlockchainStatus();
      logger.debug("Connect with plaintext");
    } catch (Exception e) {
      client.close();
      // if plaintext fails, use tls
      final String aergoNodeName = properties.getProperty("aergoNodeName");
      final InputStream serverCert = getServerCert();
      final InputStream clientCert = getClientCert();
      final InputStream clientKey = getClientKey();
      clientBuilder.withTransportSecurity(aergoNodeName, serverCert, clientCert, clientKey);
      client = clientBuilder.build();
      client.getBlockchainOperation().getBlockchainStatus();
      logger.debug("Connect with tls");
    }
    return client;
  }

  protected InputStream getServerCert() {
    return getClass().getResourceAsStream(SERVER_CRT);
  }

  protected InputStream getClientCert() {
    return getClass().getResourceAsStream(CLIENT_CRT);
  }

  protected InputStream getClientKey() {
    return getClass().getResourceAsStream(CLIENT_KEY);
  }

}
