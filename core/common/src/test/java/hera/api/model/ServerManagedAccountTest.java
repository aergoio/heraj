/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static org.junit.Assert.assertEquals;

import hera.api.encode.Base58WithCheckSum;
import java.io.IOException;
import org.junit.Test;

public class ServerManagedAccountTest {

  public static final String ENCODED_ADDRESS =
      "AmJaNDXoPbBRn9XHh9onKbDKuAzj88n5Bzt7KniYA78qUEc5EwBd";

  @Test
  public void testGetAddress() throws IOException {
    final Base58WithCheckSum encoded = () -> ENCODED_ADDRESS;
    final AccountAddress address = AccountAddress.of(encoded);
    final ServerManagedAccount account = new ServerManagedAccount(address);
    assertEquals(address, account.getAddress());
  }

  @Test
  public void testAdapt() throws IOException {
    final Base58WithCheckSum encoded = () -> ENCODED_ADDRESS;
    final AccountAddress address = AccountAddress.of(encoded);
    final ServerManagedAccount account = new ServerManagedAccount(address);
    assertEquals(account, account.adapt(Account.class).get());
    assertEquals(AccountAddress.of(encoded), account.adapt(AccountAddress.class).get());
  }

}
