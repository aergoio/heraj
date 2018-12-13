/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import hera.AbstractTestCase;
import hera.api.model.Authentication;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import org.junit.Test;

public class NaiveWalletTest extends AbstractTestCase {

  @Test
  public void testUnlock() {
    Wallet wallet = new WalletBuilder().build(WalletType.Naive);
    final AergoKey key = new AergoKeyGenerator().create();
    final String password = randomUUID().toString();
    wallet.saveKey(key, password);
    final Authentication authentication = Authentication.of(key.getAddress(), password);
    assertTrue(wallet.unlock(authentication));
  }

  @Test
  public void testLock() {
    Wallet wallet = new WalletBuilder().build(WalletType.Naive);
    final AergoKey key = new AergoKeyGenerator().create();
    final String password = randomUUID().toString();
    wallet.saveKey(key, password);
    final Authentication authentication = Authentication.of(key.getAddress(), password);
    assertTrue(wallet.lock(authentication));
  }

  @Test
  public void testBindKeyStore() {
    Wallet wallet = new WalletBuilder().build(WalletType.Naive);
    wallet.bindKeyStore(null);
  }

  @Test
  public void testSavekey() {
    Wallet wallet = new WalletBuilder().build(WalletType.Naive);
    final AergoKey key = new AergoKeyGenerator().create();
    final String password = randomUUID().toString();
    wallet.saveKey(key, password);
    assertNotNull(wallet.getAccount());
  }

  @Test
  public void testExportKey() {
    Wallet wallet = new WalletBuilder().build(WalletType.Naive);
    final AergoKey key = new AergoKeyGenerator().create();
    final String password = randomUUID().toString();
    wallet.saveKey(key, password);
    assertNotNull(wallet.exportKey(Authentication.of(key.getAddress(), password)));
  }

}
