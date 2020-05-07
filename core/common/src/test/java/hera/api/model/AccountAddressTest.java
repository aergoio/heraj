/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import hera.util.Base58Utils;
import hera.util.BytesValueUtils;
import java.util.Arrays;
import org.junit.Test;

public class AccountAddressTest {

  public final byte[] rawAddress = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
      11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33};

  public final String encodedAddress =
      "AmJaNDXoPbBRn9XHh9onKbDKuAzj88n5Bzt7KniYA78qUEc5EwBd";

  public final String encodedAddressWithoutVersion =
      Base58Utils.encodeWithCheck(("noversion" + randomUUID().toString()).getBytes());

  @Test
  public void testOfWithEncoded() {
    final AccountAddress address = AccountAddress.of(encodedAddress);
    assertTrue(Arrays.equals(rawAddress, address.getBytesValue().getValue()));
    assertEquals(encodedAddress, address.toString());
  }

  @Test
  public void shouldOfWithEncodedThrowErrorOnNoPrefix() {
    try {
      final String invalid = Base58Utils.encodeWithCheck(randomUUID().toString().getBytes());
      AccountAddress.of(invalid);
    } catch (Exception e) {
      // then
    }
  }

  @Test
  public void shouldOfWithEncodedThrowErrorOnInvalidLength() {
    try {
      final String invalid = Base58Utils.encodeWithCheck(BytesValueUtils
          .append(randomUUID().toString().getBytes(), AccountAddress.ADDRESS_PREFIX));
      AccountAddress.of(invalid);
    } catch (Exception e) {
      // then
    }
  }

  @Test
  public void testOfWithRaw() {
    final AccountAddress address = AccountAddress.of(BytesValue.of(rawAddress));
    assertTrue(Arrays.equals(rawAddress, address.getBytesValue().getValue()));
    assertEquals(encodedAddress, address.toString());
  }

  @Test
  public void shouldOfWithRawThrowErrorOnInvalidLength() {
    try {
      AccountAddress.of(BytesValue.of(randomUUID().toString().getBytes()));
    } catch (Exception e) {
      // then
    }
  }

  @Test
  public void testAdapt() {
    final AccountAddress accountAddress = AccountAddress.of(encodedAddress);
    final AccountAddress accountActual = accountAddress.adapt(AccountAddress.class);
    final ContractAddress contractActual = accountAddress.adapt(ContractAddress.class);
    final AccountAddress accountExpected = AccountAddress.of(encodedAddress);
    final ContractAddress contractExpected = ContractAddress.of(encodedAddress);
    assertEquals(accountExpected, accountActual);
    assertEquals(contractExpected, contractActual);
  }

  @Test
  public void shouldReturnEmptyStringOnEmptyOne() {
    assertEquals("", AccountAddress.EMPTY.getEncoded());
  }

}
