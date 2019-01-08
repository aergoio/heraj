/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.Account;
import hera.api.model.AccountFactory;
import hera.api.model.AccountState;
import hera.api.model.Aer;
import hera.api.model.Authentication;
import hera.api.model.Fee;
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.key.AergoKeyGenerator;
import hera.util.ThreadUtils;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;

public abstract class AbstractIT {

  protected final transient Logger logger = getLogger(getClass());

  protected static final String hostname = "localhost:7845";

  protected final String password = randomUUID().toString();

  // TODO : fee test after clarified
  protected final Fee fee = Fee.getDefaultFee();

  protected AergoClient aergoClient;

  @Before
  public void prepare() {
    aergoClient = new AergoClientBuilder()
        .withEndpoint("localhost:7845")
        .withNonBlockingConnect()
        .withTimeout(10L, TimeUnit.SECONDS)
        .build();
  }

  protected InputStream open(final String ext) {
    final String path = "/" + getClass().getName().replace('.', '/') + "." + ext;
    logger.trace("Path: {}", path);
    return getClass().getResourceAsStream(path);
  }

  protected void waitForNextBlockToGenerate() {
    ThreadUtils.trySleep(2200L);
  }

  protected List<Account> supplyAccounts() {
    final List<Account> accounts = new ArrayList<Account>();
    accounts.add(new AccountFactory().create(new AergoKeyGenerator().create()));
    accounts.add(aergoClient.getKeyStoreOperation().create(password));
    return accounts;
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
