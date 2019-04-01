/*
 * @copyright defined in LICENSE.txt
 */

package hera.spec.resolver;

import static hera.api.model.BytesValue.of;
import static hera.util.ValidationUtils.assertNotNull;
import static java.util.UUID.randomUUID;

import hera.AbstractTestCase;
import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.BytesValue;
import hera.api.model.ChainIdHash;
import hera.api.model.Fee;
import hera.api.model.RawTransaction;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import java.io.IOException;
import org.junit.Test;

public class TransactionHashResolverTest extends AbstractTestCase {

  protected static final String encodedAddress =
      "AmLo9CGR3xFZPVKZ5moSVRNW1kyscY9rVkCvgrpwNJjRUPUWadC5";

  protected final String encodedSignature =
      "381yXYmc3SjtBcSf5crwXjKZGRNSVurhTAkF8ZDwo6hWoj9uDtrf2uRTvE1KR5eTHvyPYfqVJ5Le3m6iALTtR3Yq4Q1JnnzS";

  protected final String hashWithSign = "DXmEDh6EYFnVZ8ux5xTX4U4VzF1MJcSphyYADEimAEzg";

  protected final ChainIdHash chainIdHash = new ChainIdHash(of(randomUUID().toString().getBytes()));

  protected final TransactionHashResolver resolver = new TransactionHashResolver();

  @Test
  public void testCalculateHashWithRawTx() {
    final RawTransaction rawTransaction = Transaction.newBuilder(chainIdHash)
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
    final RawTransaction rawTransaction = Transaction.newBuilder(chainIdHash)
        .from(AccountAddress.of(encodedAddress))
        .to(AccountAddress.of(encodedAddress))
        .amount("10000", Unit.AER)
        .nonce(1L)
        .fee(Fee.of(Aer.of("100", Unit.AER), 5))
        .build();

    final Signature signature = new Signature(of(randomUUID().toString().getBytes()));
    final BytesValue hash = resolver.calculateHash(rawTransaction, signature);
    assertNotNull(hash);
  }

}
