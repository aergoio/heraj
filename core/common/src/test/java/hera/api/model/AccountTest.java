/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static org.junit.Assert.assertEquals;

import hera.api.encode.Base58WithCheckSum;
import java.io.IOException;
import org.junit.Test;

public class AccountTest {

  public static final String ENCODED_ADDRESS =
      "AmJaNDXoPbBRn9XHh9onKbDKuAzj88n5Bzt7KniYA78qUEc5EwBd";

  @Test
  public void testAdapt() throws IOException {
    final Base58WithCheckSum encoded = () -> ENCODED_ADDRESS;
    final AccountAddress address = AccountAddress.of(encoded);
    final Account account = Account.of(address, null);
    assertEquals(Account.of(address, null), account.adapt(Account.class).get());
    assertEquals(AccountAddress.of(encoded), account.adapt(AccountAddress.class).get());
  }

}
