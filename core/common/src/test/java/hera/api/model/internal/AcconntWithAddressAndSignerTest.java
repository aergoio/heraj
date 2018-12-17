/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.api.encode.Base58WithCheckSum;
import hera.api.model.AccountAddress;
import hera.api.model.Transaction;
import hera.key.Signer;
import org.junit.Test;

public class AcconntWithAddressAndSignerTest {

  public static final String ENCODED_ADDRESS =
      "AmJaNDXoPbBRn9XHh9onKbDKuAzj88n5Bzt7KniYA78qUEc5EwBd";

  @Test
  public void testGetAddress() {
    final Base58WithCheckSum encoded = () -> ENCODED_ADDRESS;
    final AccountAddress address = AccountAddress.of(encoded);
    final Signer signer = mock(Signer.class);

    final AccountWithAddressAndSigner account = new AccountWithAddressAndSigner(address, signer);
    assertEquals(address, account.getAddress());
  }

  @Test
  public void testSign() {
    final Base58WithCheckSum encoded = () -> ENCODED_ADDRESS;
    final AccountAddress address = AccountAddress.of(encoded);
    final Signer signer = mock(Signer.class);
    when(signer.sign(any())).thenReturn(mock(Transaction.class));

    final AccountWithAddressAndSigner account = new AccountWithAddressAndSigner(address, signer);
    assertEquals(address, account.getAddress());
    assertNotNull(account.sign(null));
  }

  @Test
  public void testVerify() {
    final Base58WithCheckSum encoded = () -> ENCODED_ADDRESS;
    final AccountAddress address = AccountAddress.of(encoded);
    final Signer signer = mock(Signer.class);
    when(signer.verify(any())).thenReturn(true);

    final AccountWithAddressAndSigner account = new AccountWithAddressAndSigner(address, signer);
    assertTrue(account.verify(mock(Transaction.class)));
  }

}
