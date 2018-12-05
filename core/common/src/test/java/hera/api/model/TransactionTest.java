/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import hera.api.encode.Base58;
import hera.api.encode.Base58WithCheckSum;
import java.io.IOException;
import java.math.BigInteger;
import org.junit.Before;
import org.junit.Test;

public class TransactionTest {

  protected static final Base58 base58Encoded =
      () -> "AtmxbVvjDN5LYwaf5QrCZPc3FoAqUCMVegVXjf8CMCz59wL21X6j";

  protected static final Base58WithCheckSum base58WithCheckSum =
      () -> "AtmxbVvjDN5LYwaf5QrCZPc3FoAqUCMVegVXjf8CMCz59wL21X6j";

  protected Transaction transaction = null;

  @Before
  public void setup() throws IOException {
    transaction = new Transaction();
    transaction.setNonce(1L);
    transaction.setSender(AccountAddress.of(base58WithCheckSum));
    transaction.setRecipient(AccountAddress.of(base58WithCheckSum));
    transaction.setAmount(BigInteger.ONE);
    transaction.setPayload(base58Encoded.decode());
    transaction.setFee(Fee.of(BigInteger.ONE, 1L));
    transaction.setTxType(Transaction.TxType.NORMAL);
  }

  @Test
  public void testCopyOf() {
    final Transaction copy = Transaction.copyOf(transaction);
    assertEquals(transaction, copy);
  }

  @Test
  public void testCalculateHash() {
    assertNotNull(transaction.calculateHash());
  }

}
