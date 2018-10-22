/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import hera.api.encode.Base58WithCheckSum;
import hera.exception.InvalidVersionException;
import hera.util.Base58Utils;
import java.util.Arrays;
import org.junit.Test;

public class AccountAddressTest {

  public static final byte[] RAW_ADDRESS = {AccountAddress.VERSION, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
      11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33};

  public static final String ENCODED_ADDRESS =
      "AmJaNDXoPbBRn9XHh9onKbDKuAzj88n5Bzt7KniYA78qUEc5EwBd";

  public static final String ENCODED_ADDRESS_WITHOUT_VERSION =
      Base58Utils.encodeWithCheck(("noversion" + randomUUID().toString()).getBytes());

  @Test
  public void testOfWithBytesValue() {
    final AccountAddress address = AccountAddress.of(BytesValue.of(RAW_ADDRESS));
    assertTrue(Arrays.equals(RAW_ADDRESS, address.getBytesValue().getValue()));
  }

  @Test
  public void testOfWithEncoded() {
    final Base58WithCheckSum encoded = () -> ENCODED_ADDRESS;
    final AccountAddress address = AccountAddress.of(encoded);
    assertTrue(Arrays.equals(RAW_ADDRESS, address.getBytesValue().getValue()));
  }

  @Test(expected = InvalidVersionException.class)
  public void testOfWithEncodedWithoutVersion() {
    final Base58WithCheckSum encoded = () -> ENCODED_ADDRESS_WITHOUT_VERSION;
    AccountAddress.of(encoded);
  }

  @Test
  public void testAdapt() {
    final Base58WithCheckSum encoded = () -> ENCODED_ADDRESS;
    final AccountAddress address = AccountAddress.of(encoded);
    assertEquals(AccountAddress.of(encoded), address.adapt(AccountAddress.class).get());
    assertEquals(ContractAddress.of(encoded), address.adapt(ContractAddress.class).get());
  }

}
