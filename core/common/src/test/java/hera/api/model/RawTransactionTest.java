/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;

import hera.api.model.Aer.Unit;
import hera.api.model.RawTransaction.RawTransactionWithReady;
import hera.api.model.Transaction.TxType;
import org.junit.Test;

public class RawTransactionTest {

  protected final ChainIdHash chainIdHash =
      new ChainIdHash(of(randomUUID().toString().getBytes()));

  protected final String encodedAddress =
      "AtmxbVvjDN5LYwaf5QrCZPc3FoAqUCMVegVXjf8CMCz59wL21X6j";

  @Test
  public void testBuilder() {
    final RawTransactionWithReady minimum =
        RawTransaction.newBuilder(chainIdHash)
            .from(AccountAddress.of(encodedAddress))
            .to(AccountAddress.of(encodedAddress))
            .amount("10000", Unit.AER)
            .nonce(1L);
    assertNotNull(minimum.build());

    final RawTransactionWithReady maximum = minimum
        .fee(Fee.ZERO)
        .payload(BytesValue.EMPTY)
        .type(TxType.NORMAL);
    assertNotNull(maximum.build());
  }

  @Test
  public void testBuilderWithName() {
    final RawTransaction rawTransaction = RawTransaction.newBuilder(chainIdHash)
        .from("namenamenam1")
        .to("namenamenam2")
        .amount("10000", Unit.AER)
        .nonce(1L)
        .fee(Fee.of(Aer.of("100", Unit.AER), 5))
        .payload(BytesValue.EMPTY)
        .type(TxType.NORMAL)
        .build();
    assertNotNull(rawTransaction);
  }

}
