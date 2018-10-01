/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import hera.api.model.AccountAddress;
import hera.exception.InvalidVersionException;
import hera.util.Base58Utils;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Test;

public class AccountAddressTest {

  public static final byte[] RAW_ADDRESS = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,
      17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33};

  public static final String ENCODED_ADDRESS =
      "AmJaNDXoPbBRn9XHh9onKbDKuAzj88n5Bzt7KniYA78qUEc5EwBd";

  public static final String ENCODED_ADDRESS_WITHOUT_VERSION =
      Base58Utils.encodeWithCheck(("noversion" + randomUUID().toString()).getBytes());

  @Test
  public void testOfWithRaw() throws IOException {
    final AccountAddress address = AccountAddress.of(RAW_ADDRESS);
    assertTrue(Arrays.equals(RAW_ADDRESS, address.getValue()));
  }

  @Test
  public void testOfWithEncoded() throws IOException {
    final AccountAddress address = AccountAddress.of(ENCODED_ADDRESS);
    assertTrue(Arrays.equals(RAW_ADDRESS, address.getValue()));
  }

  @Test
  public void testGetEncodedValueWithRawOf() throws IOException {
    final AccountAddress address = AccountAddress.of(RAW_ADDRESS);
    assertEquals(ENCODED_ADDRESS, address.getEncodedValue());
  }

  @Test
  public void testGetEncodedValueWithEncodedOf() throws IOException {
    final AccountAddress address = AccountAddress.of(ENCODED_ADDRESS);
    assertEquals(ENCODED_ADDRESS, address.getEncodedValue());
  }

  @Test(expected = InvalidVersionException.class)
  public void testOfWithEncodedWithoutVersion() {
    AccountAddress.of(ENCODED_ADDRESS_WITHOUT_VERSION);
  }

}
