/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static org.junit.Assert.assertEquals;

import hera.api.encode.Base58WithCheckSum;
import org.junit.Test;

public class ContractAddressTest {

  public static final String ENCODED_ADDRESS =
      "AmJaNDXoPbBRn9XHh9onKbDKuAzj88n5Bzt7KniYA78qUEc5EwBd";

  @Test
  public void testAdapt() {
    final Base58WithCheckSum encoded = () -> ENCODED_ADDRESS;
    final ContractAddress address = ContractAddress.of(encoded);
    assertEquals(ContractAddress.of(encoded), address.adapt(AccountAddress.class).get());
    assertEquals(ContractAddress.of(encoded), address.adapt(ContractAddress.class).get());
  }

}
