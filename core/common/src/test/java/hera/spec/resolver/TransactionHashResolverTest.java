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
import hera.api.model.ChainIdHash;
import hera.api.model.Fee;
import hera.api.model.RawTransaction;
import hera.api.model.Signature;
import hera.api.model.TxHash;
import java.io.IOException;
import org.junit.Test;

public class TransactionHashResolverTest extends AbstractTestCase {

  protected final ChainIdHash chainIdHash = new ChainIdHash(of(randomUUID().toString().getBytes()));

  protected final AccountAddress accountAddress =
      new AccountAddress("AmLo9CGR3xFZPVKZ5moSVRNW1kyscY9rVkCvgrpwNJjRUPUWadC5");

  @Test
  public void testCalculateHashWithRawTx() {
    final RawTransaction rawTransaction = RawTransaction.newBuilder(chainIdHash)
        .from(accountAddress)
        .to(accountAddress)
        .amount("10000", Unit.AER)
        .nonce(1L)
        .fee(Fee.of(Aer.of("100", Unit.AER), 5))
        .build();

    assertNotNull(TransactionHashResolver.calculateHash(rawTransaction));
  }

  @Test
  public void testCalculateHashWithRawTxAndSignature() throws IOException {
    final RawTransaction rawTransaction = RawTransaction.newBuilder(chainIdHash)
        .from(accountAddress)
        .to(accountAddress)
        .amount("10000", Unit.AER)
        .nonce(1L)
        .fee(Fee.of(Aer.of("100", Unit.AER), 5))
        .build();

    final Signature signature = new Signature(of(randomUUID().toString().getBytes()));
    final TxHash hash = TransactionHashResolver.calculateHash(rawTransaction, signature);
    assertNotNull(hash);
  }

}
