/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.Aer.Unit;
import java.io.IOException;
import org.junit.Test;

public class RawTransactionTest extends AbstractTestCase {

  protected final ChainIdHash chainIdHash = ChainIdHash.of(BytesValue.EMPTY);

  protected final AccountAddress accountAddress =
      new AccountAddress("AmLo9CGR3xFZPVKZ5moSVRNW1kyscY9rVkCvgrpwNJjRUPUWadC5");

  @Test
  public void testCalculateHashWithRawTx() {
    final RawTransaction rawTransaction = RawTransaction.newBuilder(chainIdHash)
        .from(accountAddress)
        .to(accountAddress)
        .amount("10000", Unit.AER)
        .nonce(1L)
        .fee(Fee.of(5))
        .build();
    assertNotNull(rawTransaction.calculateHash());
  }

  @Test
  public void testCalculateHashWithRawTxAndSignature() throws IOException {
    final RawTransaction rawTransaction = RawTransaction.newBuilder(chainIdHash)
        .from(accountAddress)
        .to(accountAddress)
        .amount("10000", Unit.AER)
        .nonce(1L)
        .fee(Fee.of(5))
        .build();
    final TxHash hash = rawTransaction.calculateHash(Signature.EMPTY);
    assertNotNull(hash);
  }

}
