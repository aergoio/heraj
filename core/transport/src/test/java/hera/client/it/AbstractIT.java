/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import static org.slf4j.LoggerFactory.getLogger;

import hera.api.model.Account;
import hera.api.model.AccountState;
import hera.api.model.ClientManagedAccount;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.client.AergoClient;
import hera.key.AergoKey;
import java.io.InputStream;
import org.junit.Before;
import org.slf4j.Logger;

public abstract class AbstractIT {

  protected final transient Logger logger = getLogger(getClass());

  protected final String hostname = "localhost:7845";

  // type any encrypted private key and password of rich
  // address : AmM25FKSK1gCqSdUPjnvESsauESNgfZUauHWp7R8Un3zHffEQgTm
  protected final String richEncryptedPrivateKey =
      "47pArdc5PNS9HYY9jMMC7zAuHzytzsAuCYGm5jAUFuD3amQ4mQkvyUaPnmRVSPc2iWzVJpC9Z";
  protected final String richPassword = "password";

  protected Account rich;

  protected InputStream open(final String ext) {
    final String path = "/" + getClass().getName().replace('.', '/') + "." + ext;
    logger.trace("Path: {}", path);
    return getClass().getResourceAsStream(path);
  }

  protected void waitForNextBlockToGenerate() throws InterruptedException {
    Thread.sleep(1500L);
  }

  protected void rechargeCoin(final AergoClient aergoClient, final Account targetAccount,
      final long amount) {
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

  @Before
  public void setUp() throws Exception {
    final AergoKey key = AergoKey.of(richEncryptedPrivateKey, richPassword);
    rich = ClientManagedAccount.of(key);
  }

}
