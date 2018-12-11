/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import hera.AbstractTestCase;
import org.junit.Test;

public class ServerKeyStoreWalletTest extends AbstractTestCase {

  @Test
  public void testBindKeyStore() {
    final Wallet wallet = new WalletFactory().create(WalletType.ServerKeyStore);
    wallet.bindKeyStore(null);
  }

}
