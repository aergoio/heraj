/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import static java.math.BigInteger.valueOf;
import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountFactory;
import hera.api.model.AccountState;
import hera.api.model.Authentication;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.util.ThreadUtils;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;

public abstract class AbstractIT {

  protected final transient Logger logger = getLogger(getClass());

  protected static final String hostname = "localhost:7845";

  protected final BigInteger amount = valueOf(10L);

  // address : AmM25FKSK1gCqSdUPjnvESsauESNgfZUauHWp7R8Un3zHffEQgTm
  protected static final String richEncryptedPrivateKey =
      "47pArdc5PNS9HYY9jMMC7zAuHzytzsAuCYGm5jAUFuD3amQ4mQkvyUaPnmRVSPc2iWzVJpC9Z";
  protected static final String richPassword = "password";

  protected final AccountAddress recipient =
      AccountAddress.of(() -> "AmLbHdVs4dNpRzyLirs8cKdV26rPJJxpVXG1w2LLZ9pKfqAHHdyg");

  protected Account rich;

  protected AergoClient aergoClient;

  @Before
  public void prepare() {
    final AergoKey key = AergoKey.of(richEncryptedPrivateKey, richPassword);
    rich = new AccountFactory().create(key);

    aergoClient = new AergoClientBuilder()
        .addConfiguration("zipkin.protocol", "kafka")
        .addConfiguration("zipkin.endpoint", "localhost:9092")
        .withEndpoint("localhost:7845")
        .withNonBlockingConnect()
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

    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .sender(rich)
        .recipient(targetAccount)
        .amount(valueOf(amount))
        .nonce(richState.getNonce() + 1)
        .build();
    final Transaction signedTransaction =
        aergoClient.getAccountOperation().sign(rich, rawTransaction);
    aergoClient.getTransactionOperation().commit(signedTransaction);
  }

  protected Account createClientAccount() {
    final AergoKey key = new AergoKeyGenerator().create();
    final Account account = new AccountFactory().create(key);
    return account;
  }

  protected Account createServerAccount(final String password) {
    final Account account = aergoClient.getKeyStoreOperation().create(password);
    return account;
  }

  protected boolean unlockAccount(final Account account, final String password) {
    return aergoClient.getKeyStoreOperation()
        .unlock(Authentication.of(account.getAddress(), password));
  }

  protected boolean lockAccount(final Account account, final String password) {
    return aergoClient.getKeyStoreOperation()
        .lock(Authentication.of(account.getAddress(), password));
  }

  protected RawTransaction buildTransaction(final Account account, final AccountAddress recipient) {
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .sender(account)
        .recipient(recipient)
        .amount(amount)
        .nonce(account.incrementAndGetNonce())
        .build();
    logger.info("Raw transaction: {}", rawTransaction);
    return rawTransaction;
  }

  protected RawTransaction buildTransaction(final Account account) {
    return buildTransaction(account,
        recipient);
  }

  protected Transaction signTransaction(final Account account, RawTransaction transaction) {
    final Transaction signedTransaction =
        aergoClient.getAccountOperation().sign(account, transaction);
    logger.info("Signed transaction: {}", signedTransaction);
    return signedTransaction;
  }

  @After
  public void tearDown() {
    aergoClient.close();
  }

}
