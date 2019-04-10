/*
 * @copyright defined in LICENSE.txt
 */

package hera.spec.resolver;

import static org.junit.Assert.assertEquals;

import hera.AbstractTestCase;
import hera.api.model.AccountAddress;
import hera.key.AergoKeyGenerator;
import java.security.PublicKey;
import org.junit.Test;

public class AddressResolverTest extends AbstractTestCase {

  @Test
  public void testDeriveAndRecover() throws Exception {
    final PublicKey publicKey = new AergoKeyGenerator().create().getPublicKey();
    final AccountAddress derived = AddressResolver.deriveAddress(publicKey);
    final PublicKey recovered = AddressResolver.recoverPublicKey(derived);
    assertEquals(publicKey, recovered);
  }

}
