/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.api.model.AccountAddress;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.transaction.TxSigner;
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

  @Test
  public void testSignRawTransaction() {
    final AccountAddress address = AccountAddress.of(encodedAddress);
    final TxSigner signer = mock(TxSigner.class);
    when(signer.sign(any(RawTransaction.class))).thenReturn(mock(Transaction.class));

    final AccountWithAddressAndSigner account = new AccountWithAddressAndSigner(address, signer);
    assertEquals(address, account.getAddress());
    assertNotNull(account.sign(mock(RawTransaction.class)));
  }

}
