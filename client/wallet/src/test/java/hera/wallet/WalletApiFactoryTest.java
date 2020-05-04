/**
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.Time;
import hera.api.model.TryCountAndInterval;
import hera.keystore.InMemoryKeyStore;
import hera.keystore.KeyStore;
import org.junit.Test;

public class WalletApiFactoryTest extends AbstractTestCase {

  @Test
  public void testCreate() {
    final KeyStore keyStore = new InMemoryKeyStore();
    final WalletApiFactory walletApiFactory = new WalletApiFactory();
    assertNotNull(walletApiFactory.create(keyStore));
    assertNotNull(walletApiFactory.create(keyStore, 3, 100L));
    assertNotNull(walletApiFactory.create(keyStore, TryCountAndInterval.of(3, Time.of(100L))));
  }

}
