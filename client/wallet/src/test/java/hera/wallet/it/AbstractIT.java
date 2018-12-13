/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet.it;

import static java.math.BigInteger.valueOf;
import static org.slf4j.LoggerFactory.getLogger;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.key.AergoKey;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;
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
}