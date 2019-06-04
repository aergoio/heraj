/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import static org.junit.Assert.assertEquals;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountFactory;
import hera.api.model.AccountState;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.ChainIdHash;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.util.ThreadUtils;
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

    final ChainIdHash chainIdHash =
        aergoClient.getBlockchainOperation().getBlockchainStatus().getChainIdHash();
    aergoClient.cacheChainIdHash(chainIdHash);
  }

  protected Properties readProperties() throws IOException {
    Properties properties = new Properties();
    properties.load(getClass().getResourceAsStream(propertiesPath));
    return properties;
  }

  protected InputStream open(final String ext) {
    final String path = "/" + getClass().getName().replace('.', '/') + "." + ext;
    logger.trace("Path: {}", path);
    return getClass().getResourceAsStream(path);
  }

  protected void waitForNextBlockToGenerate() {
    ThreadUtils.trySleep(1200L);
  }

  protected Account supplyLocalAccount() {
    final Account account = new AccountFactory().create(new AergoKeyGenerator().create());
    if (isFundEnabled) {
      fund(account.getAddress());
    }
    return account;
  }

  protected AccountAddress supplyServerAccount() {
    final AccountAddress created = aergoClient.getKeyStoreOperation().create(password);
    if (isFundEnabled) {
      fund(created);
    }
    return created;
  }

  protected void fund(final AccountAddress accountAddress) {
    final Account rich = new AccountFactory().create(AergoKey.of(encrypted, password));
    final AccountState richState = aergoClient.getAccountOperation().getState(rich);
    rich.bindState(richState);
    logger.info("Rich state: {}", richState);
    final RawTransaction rawTransaction =
        RawTransaction.newBuilder(aergoClient.getCachedChainIdHash())
            .from(rich)
            .to(accountAddress)
            .amount(Aer.of("10000", Unit.AERGO))
            .nonce(rich.incrementAndGetNonce())
            .build();
    final Transaction signed = aergoClient.getAccountOperation().sign(rich, rawTransaction);
    aergoClient.getTransactionOperation().commit(signed);
    waitForNextBlockToGenerate();
  }

  protected void verifyState(final AccountState preState, final AccountState refreshed) {
    assertEquals(preState.getNonce() + 1, refreshed.getNonce());
  }

  protected boolean isDpos() {
    final String consensus =
        aergoClient.getBlockchainOperation().getBlockchainStatus().getConsensus();
    return consensus.indexOf("dpos") != -1;
  }

  @After
  public void tearDown() {
    aergoClient.close();
  }

}
