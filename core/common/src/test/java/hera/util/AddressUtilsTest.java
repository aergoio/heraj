/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static hera.util.AddressUtils.deriveAddress;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import hera.AbstractTestCase;
import hera.api.model.AccountAddress;
import hera.api.model.Identity;
import hera.key.AergoKeyGenerator;
import org.junit.Test;

public class AddressUtilsTest extends AbstractTestCase {

  @Test
  public void testDeriveAddressFromIdentity() {
    final Identity validIdentity = new AergoKeyGenerator().create().getAddress();
    final AccountAddress derived = deriveAddress(validIdentity);
    assertNotNull(derived);

    try {
      final Identity invalidIdentity = new Identity() {
        @Override
        public String getInfo() {
          return randomUUID().toString();
        }
      };
      deriveAddress(invalidIdentity);
      fail();
    } catch (Exception e) {
      // good we expected this
    }
  }

}
