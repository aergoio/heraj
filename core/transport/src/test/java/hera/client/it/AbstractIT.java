/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

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
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;

public abstract class AbstractIT {

  protected final transient Logger logger = getLogger(getClass());

  protected final NonceProvider nonceProvider = new SimpleNonceProvider();

  protected final String propertiesPath = "/it.properties";

  protected String hostname;

  protected String[] peerIds;

  protected String encrypted;

  protected String password;

  protected boolean isFundEnabled;

  protected AergoClient aergoClient;

  @Before
  public void setUp() throws Exception {
    final Properties properties = readProperties();
    hostname = (String) properties.get("hostname");
    peerIds = ((String) properties.get("peer")).split(",");

    encrypted = (String) properties.get("encrypted");
    password = (String) properties.get("password");
    isFundEnabled = Boolean.valueOf((String) properties.get("isFundEnabled"));

    final boolean isTlsEnabled = Boolean.valueOf((String) properties.get("isTlsEnabled"));

    final AergoClientBuilder clientBuilder = new AergoClientBuilder()
        .withNonBlockingConnect()
        .withTimeout(3, TimeUnit.SECONDS)
        .withEndpoint(hostname);

    if (isTlsEnabled) {
      clientBuilder.withTransportSecurity(
          "aergo.node",
          openInPackage("server.crt"),
          openInPackage("client.crt"),
          openInPackage("client.pem"));
    }

    this.aergoClient = clientBuilder.build();

    // setup rich
    final AergoKey rich = AergoKey.of(encrypted, password);
    final AccountState richState = aergoClient.getAccountOperation().getState(rich.getPrincipal());
    nonceProvider.bindNonce(richState);
    logger.info("Rich state: {}", richState);

    final ChainIdHash chainIdHash =
        aergoClient.getBlockchainOperation().getBlockchainStatus().getChainIdHash();
    aergoClient.cacheChainIdHash(chainIdHash);
  }

  protected Properties readProperties() throws IOException {
    Properties properties = new Properties();
    properties.load(getClass().getResourceAsStream(propertiesPath));
    return properties;
  }

  protected InputStream openInPackage(final String ext) {
    final String path = "/" + getClass().getPackage().getName().replace('.', '/') + "/" + ext;
    logger.trace("Path: {}", path);
    return getClass().getResourceAsStream(path);
  }

  protected InputStream open(final String ext) {
    final String path = "/" + getClass().getName().replace('.', '/') + "." + ext;
    logger.trace("Path: {}", path);
    return getClass().getResourceAsStream(path);
  }

  protected void waitForNextBlockToGenerate() {
    ThreadUtils.trySleep(1200L);
  }

  protected AergoKey createNewKey() {
    final AergoKey aergoKey = new AergoKeyGenerator().create();
    if (isFundEnabled) {
      fund(aergoKey.getAddress());
    }
    return aergoKey;
  }

  protected void fund(final AccountAddress accountAddress) {
    final AergoKey rich = AergoKey.of(encrypted, password);
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .chainIdHash(aergoClient.getCachedChainIdHash())
        .from(rich.getPrincipal())
        .to(accountAddress)
        .amount(Aer.of("10000", Unit.AERGO))
        .nonce(nonceProvider.incrementAndGetNonce(rich.getPrincipal()))
        .build();
    final Transaction signed = rich.sign(rawTransaction);
    aergoClient.getTransactionOperation().commit(signed);
    waitForNextBlockToGenerate();
  }

  @After
  public void tearDown() {
    aergoClient.close();
  }

  protected String randomName() {
    return randomUUID().toString().substring(0, 12).replace('-', 'a');
  }

}
