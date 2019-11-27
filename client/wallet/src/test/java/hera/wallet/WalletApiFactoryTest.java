/**
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.keystore.InMemoryKeyStore;
import hera.keystore.KeyStore;
import org.junit.Test;

public class WalletApiFactoryTest extends AbstractTestCase {

  @Test
  public void testCreate() {
    final KeyStore keyStore = new InMemoryKeyStore();
    final WalletApi walletApi = new WalletApiFactory().create(keyStore);
    assertNotNull(walletApi);
  }

}
