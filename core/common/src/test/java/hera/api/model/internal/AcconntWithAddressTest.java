/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model.internal;

import static org.junit.Assert.assertEquals;

import hera.api.model.Account;
import hera.api.model.AccountAddress;
import org.junit.Test;

public class AcconntWithAddressTest {

  protected static final String encodedAddress =
      "AmJaNDXoPbBRn9XHh9onKbDKuAzj88n5Bzt7KniYA78qUEc5EwBd";

  @Test
  public void testGetAddress() {
    final AccountAddress address = AccountAddress.of(encodedAddress);
    final Account account = new AccountWithAddress(address);
    assertEquals(address, account.getAddress());
  }

}
