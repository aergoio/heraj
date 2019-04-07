/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model.internal;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.api.model.RawTransaction;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.key.Signer;
import org.junit.Test;

public class AcconntWithAddressAndSignerTest {

  protected final String encodedAddress =
      "AmJaNDXoPbBRn9XHh9onKbDKuAzj88n5Bzt7KniYA78qUEc5EwBd";

  @Test
  public void testGetAddress() {
    final AccountAddress address = AccountAddress.of(encodedAddress);
    final Signer signer = mock(Signer.class);

    final AccountWithAddressAndSigner account = new AccountWithAddressAndSigner(address, signer);
    assertEquals(address, account.getAddress());
  }

  @Test
  public void testSignRawTransaction() {
    final AccountAddress address = AccountAddress.of(encodedAddress);
    final Signer signer = mock(Signer.class);
    when(signer.sign(any(RawTransaction.class))).thenReturn(mock(Transaction.class));

    final AccountWithAddressAndSigner account = new AccountWithAddressAndSigner(address, signer);
    assertEquals(address, account.getAddress());
    assertNotNull(account.sign(mock(RawTransaction.class)));
  }

  @Test
  public void testSignPlainText() {
    final AccountAddress address = AccountAddress.of(encodedAddress);
    final Signer signer = mock(Signer.class);
    when(signer.sign(any(BytesValue.class))).thenReturn(mock(Signature.class));

    final AccountWithAddressAndSigner account = new AccountWithAddressAndSigner(address, signer);
    assertEquals(address, account.getAddress());
    assertNotNull(account.sign(BytesValue.EMPTY));
  }

  @Test
  public void testSignMessage() {
    final AccountAddress address = AccountAddress.of(encodedAddress);
    final Signer signer = mock(Signer.class);
    when(signer.signMessage(anyString())).thenReturn(randomUUID().toString());

    final AccountWithAddressAndSigner account = new AccountWithAddressAndSigner(address, signer);
    assertEquals(address, account.getAddress());
    assertNotNull(account.signMessage(randomUUID().toString()));
  }

}
