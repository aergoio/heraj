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

  protected AddressResolver resolver = new AddressResolver();

  @Test
  public void testDeriveAndRecover() throws Exception {
    final PublicKey publicKey = new AergoKeyGenerator().create().getPublicKey();
    final AccountAddress derived = resolver.deriveAddress(publicKey);
    final PublicKey recovered = resolver.recoverPublicKey(derived);
    assertEquals(publicKey, recovered);
  }

}
