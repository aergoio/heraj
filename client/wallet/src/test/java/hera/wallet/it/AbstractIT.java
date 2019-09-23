/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet.it;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.transaction.NonceProvider;
import hera.api.transaction.SimpleNonceProvider;
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.util.ThreadUtils;
import hera.wallet.Wallet;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;

public abstract class AbstractIT {

  protected final transient Logger logger = getLogger(getClass());

  protected final String propertiesPath = "/it.properties";

  protected final NonceProvider nonceProvider = new SimpleNonceProvider();

  protected String hostname;

  protected String encrypted;

  protected String password;

  protected String[] peerIds;

  protected boolean isFundEnabled;

  protected AergoClient aergoClient;

  @Before
  public void setUp() throws Exception {
    final Properties properties = readProperties();
    hostname = (String) properties.get("hostname");
    encrypted = (String) properties.get("encrypted");
    password = (String) properties.get("password");
    peerIds = ((String) properties.get("peer")).split(",");
    isFundEnabled = Boolean.valueOf((String) properties.get("enablefund"));

    aergoClient = new AergoClientBuilder()
        .withNonBlockingConnect()
        .withEndpoint(hostname)
        .build();
    aergoClient.cacheChainIdHash(aergoClient.getBlockchainOperation().getChainIdHash());

    final AergoKey rich = AergoKey.of(encrypted, password);
    final AccountState richState = aergoClient.getAccountOperation().getState(rich.getPrincipal());
    logger.info("Rich state: {}", richState);
    nonceProvider.bindNonce(richState);
  }

  protected Properties readProperties() throws IOException {
    Properties properties = new Properties();
    properties.load(getClass().getResourceAsStream(propertiesPath));
    return properties;
  }


  protected InputStream open(final String ext) {
    logger.trace("Path: {}", getPath(ext));
    return getClass().getResourceAsStream(getPath(ext));
  }

  protected String getPath(final String ext) {
    return "/" + getClass().getName().replace('.', '/') + "." + ext;
  }

  protected void waitForNextBlockToGenerate() {
    ThreadUtils.trySleep(2200L);
  }

  protected AergoKey supplyKeyAergo(final Wallet wallet) {
    final AergoKey key = new AergoKeyGenerator().create();
    if (isFundEnabled) {
      fund(key.getAddress());
    }
    return key;
  }

  private void fund(final AccountAddress poor) {
    final AergoKey rich = AergoKey.of(encrypted, password);
    final RawTransaction rawTransaction =
        RawTransaction.newBuilder(aergoClient.getCachedChainIdHash())
            .from(rich.getAddress())
            .to(poor)
            .amount(Aer.of("10000", Unit.AERGO))
            .nonce(nonceProvider.incrementAndGetNonce(rich.getAddress()))
            .build();
    final Transaction signed = rich.sign(rawTransaction);
    aergoClient.getTransactionOperation().commit(signed);
    waitForNextBlockToGenerate();
  }

  @After
  public void tearDown() {
    aergoClient.close();
  }

}
