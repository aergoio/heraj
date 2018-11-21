/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.Authentication;
import hera.api.model.ClientManagedAccount;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.util.ThreadUtils;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;

public abstract class AbstractIT {

  protected final transient Logger logger = getLogger(getClass());

  protected static final String hostname = "localhost:7845";

  protected final long amount = 10L;

  // address : AmM25FKSK1gCqSdUPjnvESsauESNgfZUauHWp7R8Un3zHffEQgTm
  protected static final String richEncryptedPrivateKey =
      "47pArdc5PNS9HYY9jMMC7zAuHzytzsAuCYGm5jAUFuD3amQ4mQkvyUaPnmRVSPc2iWzVJpC9Z";
  protected static final String richPassword = "password";

  protected Account rich;

  protected AergoClient aergoClient;

  @Before
  public void prepare() {
    final AergoKey key = AergoKey.of(richEncryptedPrivateKey, richPassword);
    rich = ClientManagedAccount.of(key);

    aergoClient = new AergoClientBuilder()
        .addConfiguration("zipkin.protocol", "kafka")
        .addConfiguration("zipkin.endpoint", "localhost:9092")
        .withEndpoint("localhost:7845")
        .withNettyConnect()
        .withTracking()
        .withTimeout(10L, TimeUnit.SECONDS)
        .build();
  }

  protected InputStream open(final String ext) {
    final String path = "/" + getClass().getName().replace('.', '/') + "." + ext;
    logger.trace("Path: {}", path);
    return getClass().getResourceAsStream(path);
  }

  protected void waitForNextBlockToGenerate() {
    ThreadUtils.trySleep(1300L);
  }

  protected void rechargeCoin(final Account targetAccount, final long amount) {
    final AccountState richState = aergoClient.getAccountOperation().getState(rich);

    final Transaction transaction = new Transaction();
    transaction.setNonce(richState.getNonce() + 1);
    transaction.setAmount(amount);
    transaction.setSender(rich);
    transaction.setRecipient(targetAccount);
    final Signature signature = aergoClient.getAccountOperation().sign(rich, transaction);
    transaction.setSignature(signature);

    aergoClient.getTransactionOperation().commit(transaction);
  }

  protected Account createClientAccount() {
    final AergoKey key = new AergoKeyGenerator().create();
    final Account account = ClientManagedAccount.of(key);
    return account;
  }

  protected Account createServerAccount(final String password) {
    final Account account = aergoClient.getAccountOperation().create(password);
    return account;
  }

  protected boolean unlockAccount(final Account account, final String password) {
    return aergoClient.getAccountOperation()
        .unlock(Authentication.of(account.getAddress(), password));
  }

  protected boolean lockAccount(final Account account, final String password) {
    return aergoClient.getAccountOperation()
        .lock(Authentication.of(account.getAddress(), password));
  }

  protected Transaction buildTransaction(final Account account, final AccountAddress recipient) {
    final Transaction transaction = new Transaction();
    transaction.setNonce(account.nextNonce());
    transaction.setAmount(amount);
    transaction.setSender(account);
    transaction.setRecipient(recipient);
    logger.info("Raw transaction: {}", transaction);
    return transaction;
  }

  protected Transaction buildTransaction(final Account account) {
    return buildTransaction(account,
        AccountAddress.of(() -> "AmLbHdVs4dNpRzyLirs8cKdV26rPJJxpVXG1w2LLZ9pKfqAHHdyg"));
  }

  protected void signTransaction(final Account account, Transaction transaction) {
    final Signature signature = aergoClient.getAccountOperation().sign(account, transaction);
    transaction.setSignature(signature);
    logger.info("Signed transaction: {}", transaction);
  }

  @After
  public void tearDown() {
    aergoClient.close();
  }

}
