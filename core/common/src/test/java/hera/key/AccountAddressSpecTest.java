/*
 * @copyright defined in LICENSE.txt
 */

package hera.key;

import static org.junit.Assert.assertEquals;

import hera.AbstractTestCase;
import hera.api.model.AccountAddress;
import java.security.PublicKey;
import org.junit.Test;

public class AccountAddressSpecTest extends AbstractTestCase {

  @Test
  public void testDeriveAndRecover() throws Exception {
    final PublicKey publicKey = new AergoKeyGenerator().create().getPublicKey();
    final AccountAddress derived = AccountAddressSpec.deriveAddress(publicKey);
    final PublicKey recovered = AccountAddressSpec.recoverPublicKey(derived);
    assertEquals(publicKey, recovered);
  }

}
