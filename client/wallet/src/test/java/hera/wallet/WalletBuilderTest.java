/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

public class WalletBuilderTest extends AbstractTestCase {

  @Test
  public void testCreate() {
    final WalletBuilder builder = new WalletBuilder()
        .withBlockingConnect()
        .withEndpoint("localhost:7845")
        .withRefresh(3, 1000L, TimeUnit.MILLISECONDS)
        .withTimeout(1000L, TimeUnit.MILLISECONDS)
        .withRetry(3, 5, TimeUnit.SECONDS)
        .addConfiguration("key", "value");

    assertNotNull(builder.build(WalletType.Naive));
    assertNotNull(builder.build(WalletType.ServerKeyStore));
  }

}
