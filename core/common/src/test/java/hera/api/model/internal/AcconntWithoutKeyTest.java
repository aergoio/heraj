/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model.internal;

import static org.junit.Assert.assertEquals;

import hera.api.encode.Base58WithCheckSum;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import org.junit.Test;

public class AcconntWithoutKeyTest {

  public static final String ENCODED_ADDRESS =
      "AmJaNDXoPbBRn9XHh9onKbDKuAzj88n5Bzt7KniYA78qUEc5EwBd";

  @Test
  public void testGetAddress() {
    final Base58WithCheckSum encoded = () -> ENCODED_ADDRESS;
    final AccountAddress address = AccountAddress.of(encoded);
    final Account account = new AccountWithoutKey(address);
    assertEquals(address, account.getAddress());
  }

}
