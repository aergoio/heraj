/*
 * @copyright defined in LICENSE.txt
 */

package hera.it;

import static java.util.UUID.randomUUID;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.ChainIdHash;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.transaction.NonceProvider;
import hera.api.transaction.SimpleNonceProvider;
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.util.ThreadUtils;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;

public abstract class AbstractIT {

  protected final transient Logger logger = getLogger(getClass());

  protected final String aergoProperties = "aergo.properties";
  protected final String serverCrtFile = "server.crt";
  protected final String clientCrtFile = "client.crt";
  protected final String clientKeyFile = "client.pem";

  protected AergoClient aergoClient;
  protected String hostname;
  protected AergoKey genesisKey;
  protected final NonceProvider nonceProvider = new SimpleNonceProvider();

  @Before
  public void setUp() throws Exception {
    final Properties properties = new Properties();
    try (final InputStream in = getClass().getResourceAsStream(aergoProperties)) {
      properties.load(in);
    }

    // setup client (try plaintext first)
    hostname = properties.getProperty("endpoint");
    AergoClientBuilder clientBuilder = new AergoClientBuilder()
        .withNonBlockingConnect()
        .withTimeout(1000, TimeUnit.MILLISECONDS)
        .withRetry(3, 500L, TimeUnit.MILLISECONDS)
        .withEndpoint(hostname)
        .withPlainText();
    AergoClient candidate = clientBuilder.build();
    try {
      candidate.getBlockchainOperation().getBlockchainStatus();
      logger.trace("Connect with plaintext success");
    } catch (Exception e) {
      // if plaintext fails, use tls
      final String aergoNodeName = properties.getProperty("aergoNodeName");
      final InputStream serverCert = getClass().getResourceAsStream(serverCrtFile);
      final InputStream clientCert = getClass().getResourceAsStream(clientCrtFile);
      final InputStream clientKey = getClass().getResourceAsStream(clientKeyFile);
      clientBuilder.withTransportSecurity(aergoNodeName, serverCert, clientCert, clientKey);
      candidate = clientBuilder.build();
      candidate.getBlockchainOperation().getBlockchainStatus();
      logger.trace("Connect with tls success");
    }
    this.aergoClient = candidate;

    // load genesis key
    final String genesisEncrypted = properties.getProperty("genesisEncrypted");
    final String genesisPassword = properties.getProperty("genesisPassword");
    this.genesisKey = AergoKey.of(genesisEncrypted, genesisPassword);
    logger.trace("Genesis key: {}", this.genesisKey);
    final AccountState genesisState =
        aergoClient.getAccountOperation().getState(this.genesisKey.getAddress());
    this.nonceProvider.bindNonce(genesisState);

    // cache chain id hash
    final ChainIdHash chainIdHash =
        aergoClient.getBlockchainOperation().getBlockchainStatus().getChainIdHash();
    aergoClient.cacheChainIdHash(chainIdHash);
    logger.trace("Cached chain id hash: {}", aergoClient.getCachedChainIdHash());
  }

  protected InputStream open(final String ext) {
    final String path = "/" + getClass().getName().replace('.', '/') + "." + ext;
    logger.trace("Path: {}", path);
    return getClass().getResourceAsStream(path);
  }

  protected void waitForNextBlockToGenerate() {
    ThreadUtils.trySleep(2200L);
  }

  protected AergoKey createNewKey() {
    final AergoKey newKey = new AergoKeyGenerator().create();
    fund(newKey.getAddress());
    return newKey;
  }

  protected void fund(final AccountAddress accountAddress) {
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .chainIdHash(aergoClient.getCachedChainIdHash())
        .from(this.genesisKey.getPrincipal())
        .to(accountAddress)
        .amount(Aer.of("10000", Unit.AERGO))
        .nonce(nonceProvider.incrementAndGetNonce(this.genesisKey.getPrincipal()))
        .build();
    final Transaction signed = this.genesisKey.sign(rawTransaction);
    aergoClient.getTransactionOperation().commit(signed);
    waitForNextBlockToGenerate();
  }

  protected String randomName() {
    return randomUUID().toString().substring(0, 12).replace('-', 'a');
  }

  @After
  public void tearDown() {
    if (null != aergoClient) {
      aergoClient.close();
    }
  }

}
