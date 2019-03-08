/*
 * @copyright defined in LICENSE.txt
 */

package hera.spec.resolver;

import static hera.util.EncodingUtils.decodeBase58;
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
import hera.util.Base58Utils;
import java.io.IOException;
import org.junit.Test;

public class TransactionHashResolverTest extends AbstractTestCase {

  protected static final String encodedAddress =
      "AmLo9CGR3xFZPVKZ5moSVRNW1kyscY9rVkCvgrpwNJjRUPUWadC5";

  protected final String encodedSignature =
      "381yXYmc3SjtBcSf5crwXjKZGRNSVurhTAkF8ZDwo6hWoj9uDtrf2uRTvE1KR5eTHvyPYfqVJ5Le3m6iALTtR3Yq4Q1JnnzS";

  protected final String hashWithSign = "DXmEDh6EYFnVZ8ux5xTX4U4VzF1MJcSphyYADEimAEzg";

  protected final TransactionHashResolver resolver = new TransactionHashResolver();

  @Test
  public void testCalculateHashWithRawTx() {
    final RawTransaction rawTransaction = Transaction.newBuilder()
        .from(AccountAddress.of(encodedAddress))
        .to(AccountAddress.of(encodedAddress))
        .amount("10000", Unit.AER)
        .nonce(1L)
        .fee(Fee.of(Aer.of("100", Unit.AER), 5))
        .build();

    assertNotNull(resolver.calculateHash(rawTransaction));
  }

  @Test
  public void testCalculateHashWithRawTxAndSignature() throws IOException {
    final RawTransaction rawTransaction = Transaction.newBuilder()
        .from(AccountAddress.of(encodedAddress))
        .to(AccountAddress.of(encodedAddress))
        .amount("10000", Unit.AER)
        .nonce(1L)
        .fee(Fee.of(Aer.of("100", Unit.AER), 5))
        .build();

    final Signature signature = Signature.of(decodeBase58(encodedSignature));
    final BytesValue expected = new BytesValue(Base58Utils.decode(hashWithSign));
    final BytesValue actual = resolver.calculateHash(rawTransaction, signature);
    assertEquals(expected, actual);
  }

}
