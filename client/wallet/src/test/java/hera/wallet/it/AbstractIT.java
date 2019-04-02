/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet.it;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountFactory;
import hera.api.model.AccountState;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
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
    ThreadUtils.trySleep(1200L);
  }

  protected AergoKey supplyKeyWithAergo(final Wallet wallet) {
    final AergoKey key = new AergoKeyGenerator().create();
    if (isFundEnabled) {
      fund(key.getAddress());
    }
    return key;
  }

  private void fund(final AccountAddress poor) {
    final Account rich = new AccountFactory().create(AergoKey.of(encrypted, password));
    final AccountState richState = aergoClient.getAccountOperation().getState(rich);
    rich.bindState(richState);
    logger.info("Rich state: {}", richState);
    final RawTransaction rawTransaction =
        RawTransaction.newBuilder(aergoClient.getCachedChainIdHash())
            .from(rich)
            .to(poor)
            .amount(Aer.of("10000", Unit.AERGO))
            .nonce(rich.incrementAndGetNonce())
            .build();
    final Transaction signed = aergoClient.getAccountOperation().sign(rich, rawTransaction);
    aergoClient.getTransactionOperation().commit(signed);
    waitForNextBlockToGenerate();
  }

  @After
  public void tearDown() {
    aergoClient.close();
  }

}
