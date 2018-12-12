/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

public class WalletFactoryTest extends AbstractTestCase {

  @Test
  public void testCreate() {
    final WalletFactory factory = new WalletFactory()
        .withBlockingConnect()
        .withEndpoint("localhost:7845")
        .withNonceRefresh(3)
        .withTimeout(1000L, TimeUnit.MILLISECONDS)
        .withRetry(3, 5, TimeUnit.SECONDS)
        .withTracking()
        .addConfiguration("key", "value");

    assertNotNull(factory.create(WalletType.Naive));
    assertNotNull(factory.create(WalletType.ServerKeyStore));
  }

}
