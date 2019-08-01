/*
 * @copyright defined in LICENSE.txt
 */

package hera.spec.resolver;

import static hera.api.model.BytesValue.of;
import static hera.util.ValidationUtils.assertNotNull;
import static java.util.UUID.randomUUID;

import hera.AbstractTestCase;
import hera.api.model.AccountAddress;
import hera.api.model.Aer.Unit;
import hera.api.model.BytesValue;
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
        .fee(Fee.of(5))
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
        .fee(Fee.of(5))
        .build();

    final BytesValue signValue = BytesValue.of(randomUUID().toString().getBytes());
    final Signature sign = Signature.newBuilder().sign(signValue).build();
    final TxHash hash = TransactionHashResolver.calculateHash(rawTransaction, sign);
    assertNotNull(hash);
  }

}
