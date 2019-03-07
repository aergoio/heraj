/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertTrue;

import hera.exception.InvalidVersionException;
import hera.spec.AddressSpec;
import hera.util.Base58Utils;
import java.util.Arrays;
import org.junit.Test;

public class AccountAddressTest {

  public final byte[] rawAddress = {AddressSpec.PREFIX, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
      11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33};

  public final String encodedAddress =
      "AmJaNDXoPbBRn9XHh9onKbDKuAzj88n5Bzt7KniYA78qUEc5EwBd";

  public final String encodedAddressWithoutVersion =
      Base58Utils.encodeWithCheck(("noversion" + randomUUID().toString()).getBytes());

  @Test
  public void testOfWithBytesValue() {
    final AccountAddress address = AccountAddress.of(BytesValue.of(rawAddress));
    assertTrue(Arrays.equals(rawAddress, address.getBytesValue().getValue()));
  }

  @Test
  public void testOfWithEncoded() {
    final AccountAddress address = AccountAddress.of(encodedAddress);
    assertTrue(Arrays.equals(rawAddress, address.getBytesValue().getValue()));
  }

  @Test(expected = InvalidVersionException.class)
  public void testOfWithEncodedWithoutVersion() {
    AccountAddress.of(encodedAddressWithoutVersion);
  }

}
