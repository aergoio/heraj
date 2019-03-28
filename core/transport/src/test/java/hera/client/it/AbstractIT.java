/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import static org.junit.Assert.assertEquals;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.Account;
import hera.api.model.AccountFactory;
import hera.api.model.AccountState;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.Authentication;
import hera.api.model.Fee;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.util.ThreadUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;

public abstract class AbstractIT {

  protected final transient Logger logger = getLogger(getClass());

  protected final String propertiesPath = "/it.properties";

  protected final Fee fee = Fee.getDefaultFee();

  protected String hostname;

  protected String encrypted;

  protected String password;

  protected String peer;

  protected AergoClient aergoClient;

  @Before
  public void setUp() throws Exception {
    final Properties properties = readProperties();
    hostname = (String) properties.get("hostname");
    encrypted = (String) properties.get("encrypted");
    password = (String) properties.get("password");
    peer = (String) properties.get("peer");
    aergoClient = new AergoClientBuilder()
        .withNonBlockingConnect()
        .withEndpoint(hostname)
        .build();
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

  protected List<Account> supplyAccounts() {
    final List<Account> accounts = new ArrayList<Account>();
    accounts.add(supplyLocalAccount());
    accounts.add(supplyServerAccount());
    return accounts;
  }

  protected Account supplyLocalAccount() {
    final Account account = new AccountFactory().create(new AergoKeyGenerator().create());
    // fund(account);
    return account;
  }

  protected Account supplyServerAccount() {
    final Account account = aergoClient.getKeyStoreOperation().create(password);
    // fund(account);
    return account;
  }

  private void fund(final Account account) {
    final Account rich = new AccountFactory().create(AergoKey.of(encrypted, password));
    final AccountState richState = aergoClient.getAccountOperation().getState(rich);
    rich.bindState(richState);
    logger.info("Rich state: {}", richState);
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .from(rich)
        .to(account)
        .amount(Aer.of("10", Unit.AERGO))
        .nonce(rich.incrementAndGetNonce())
        .build();
    final Transaction signed = aergoClient.getAccountOperation().sign(rich, rawTransaction);
    aergoClient.getTransactionOperation().commit(signed);
    waitForNextBlockToGenerate();
  }

  protected boolean unlockAccount(final Account account, final String password) {
    if (null == account.getKey()) {
      return aergoClient.getKeyStoreOperation()
          .unlock(Authentication.of(account.getAddress(), password));
    }
    return true;
  }

  protected boolean lockAccount(final Account account, final String password) {
    if (null == account.getKey()) {
      return aergoClient.getKeyStoreOperation()
          .lock(Authentication.of(account.getAddress(), password));
    }
    return true;
  }

  protected void verifyState(final AccountState preState, final AccountState refreshed,
      final Aer sendAmount) {
    assertEquals(preState.getNonce() + 1, refreshed.getNonce());
    assertEquals(preState.getBalance().subtract(sendAmount.add(fee.getPrice())),
        refreshed.getBalance());
  }

  @After
  public void tearDown() {
    aergoClient.close();
  }

}
