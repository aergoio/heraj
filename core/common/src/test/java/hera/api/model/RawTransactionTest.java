/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static org.junit.Assert.assertNotNull;

import hera.api.model.Aer.Unit;
import hera.api.model.RawTransaction.RawTransactionWithSenderAndRecipientAndAmountAndNonce;
import hera.api.model.RawTransaction.RawTransactionWithSenderAndRecipientAndAmountAndNonceAndFee;
import hera.api.model.RawTransaction.RawTransactionWithSenderAndRecipientAndAmountAndNonceAndFeeAndPayload;
import org.junit.Test;

public class RawTransactionTest {

  protected static final String encodedAddress =
      "AtmxbVvjDN5LYwaf5QrCZPc3FoAqUCMVegVXjf8CMCz59wL21X6j";

  @Test
  public void testBuilder() {
    final RawTransactionWithSenderAndRecipientAndAmountAndNonce minimum =
        RawTransaction.newBuilder()
            .sender(AccountAddress.of(encodedAddress))
            .recipient(AccountAddress.of(encodedAddress))
            .amount("10000", Unit.AER)
            .nonce(1L);
    assertNotNull(minimum.build());

    final RawTransactionWithSenderAndRecipientAndAmountAndNonceAndFee minimumWithFee =
        minimum.fee(Fee.of(Aer.of("100", Unit.AER), 5));
    assertNotNull(minimumWithFee.build());

    final RawTransactionWithSenderAndRecipientAndAmountAndNonceAndFeeAndPayload maximum =
        minimumWithFee.payload(BytesValue.EMPTY);
    assertNotNull(maximum.build());
  }

  @Test
  public void testBuilderWithName() {
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .sender("namenamenam1")
        .recipient("namenamenam2")
        .amount("10000", Unit.AER)
        .nonce(1L)
        .fee(Fee.of(Aer.of("100", Unit.AER), 5))
        .payload(BytesValue.EMPTY)
        .build();
    assertNotNull(rawTransaction);
  }

}
