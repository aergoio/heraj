/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static hera.api.model.BytesValue.of;
import static hera.util.ValidationUtils.assertNotNull;
import static org.junit.Assert.assertEquals;

import hera.AbstractTestCase;
import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.BytesValue;
import hera.api.model.Fee;
import hera.api.model.RawTransaction;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import java.io.IOException;
import org.junit.Test;

public class TransactionUtilsTest extends AbstractTestCase {

  protected static final String encodedAddress =
      "AmLo9CGR3xFZPVKZ5moSVRNW1kyscY9rVkCvgrpwNJjRUPUWadC5";

  protected final String encodedSignature =
      "381yXYmc3SjtBcSf5crwXjKZGRNSVurhTAkF8ZDwo6hWoj9uDtrf2uRTvE1KR5eTHvyPYfqVJ5Le3m6iALTtR3Yq4Q1JnnzS";

  protected final String hashWithSign = "DXmEDh6EYFnVZ8ux5xTX4U4VzF1MJcSphyYADEimAEzg";

  @Test
  public void testCalculateHashWithRawTx() {
    final RawTransaction rawTransaction = Transaction.newBuilder()
        .sender(AccountAddress.of(() -> encodedAddress))
        .recipient(AccountAddress.of(() -> encodedAddress))
        .amount("10000", Unit.AER)
        .nonce(1L)
        .fee(Fee.of(Aer.of("100", Unit.AER), 5))
        .build();

    assertNotNull(TransactionUtils.calculateHash(rawTransaction));
  }

  @Test
  public void testCalculateHashWithRawTxAndSignature() throws IOException {
    final RawTransaction rawTransaction = Transaction.newBuilder()
        .sender(AccountAddress.of(() -> encodedAddress))
        .recipient(AccountAddress.of(() -> encodedAddress))
        .amount("10000", Unit.AER)
        .nonce(1L)
        .fee(Fee.of(Aer.of("100", Unit.AER), 5))
        .build();

    final Signature signature = Signature.of(BytesValue.of(Base58Utils.decode(
        encodedSignature)));
    final TxHash expected = TxHash.of(of(Base58Utils.decode(hashWithSign)));
    final TxHash actual = TransactionUtils.calculateHash(rawTransaction, signature);
    assertEquals(expected, actual);
  }

}
