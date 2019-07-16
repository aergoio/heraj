/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model.internal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import hera.api.model.AccountAddress;
import hera.key.TxSigner;
import org.junit.Test;

public class AcconntWithAddressAndSignerTest {

  protected final String encodedAddress =
      "AmJaNDXoPbBRn9XHh9onKbDKuAzj88n5Bzt7KniYA78qUEc5EwBd";

  @Test
  public void testGetAddress() {
    final AccountAddress address = AccountAddress.of(encodedAddress);
    final TxSigner signer = mock(TxSigner.class);

    final AccountWithAddressAndSigner account = new AccountWithAddressAndSigner(address, signer);
    assertEquals(address, account.getAddress());
  }

}
