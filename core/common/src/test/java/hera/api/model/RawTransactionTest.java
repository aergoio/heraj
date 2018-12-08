/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static org.junit.Assert.assertNotNull;

import hera.api.encode.Base58WithCheckSum;
import hera.api.model.RawTransaction.RawTransactionWithSenderAndRecipientAndAmountAndNonce;
import hera.api.model.RawTransaction.RawTransactionWithSenderAndRecipientAndAmountAndNonceAndFee;
import hera.api.model.RawTransaction.RawTransactionWithSenderAndRecipientAndAmountAndNonceAndFeeAndPayload;
import org.junit.Test;

public class RawTransactionTest {

  protected static final Base58WithCheckSum base58WithCheckSum =
      () -> "AtmxbVvjDN5LYwaf5QrCZPc3FoAqUCMVegVXjf8CMCz59wL21X6j";

  @Test
  public void testBuilder() {
    final RawTransactionWithSenderAndRecipientAndAmountAndNonce minimum =
        RawTransaction.newBuilder()
            .sender(AccountAddress.of(base58WithCheckSum))
            .recipient(AccountAddress.of(base58WithCheckSum))
            .amount("10000")
            .nonce(1L);
    assertNotNull(minimum.build());

    final RawTransactionWithSenderAndRecipientAndAmountAndNonceAndFee minimumWithFee =
        minimum.fee(Fee.of("100", 5));
    assertNotNull(minimumWithFee.build());

    final RawTransactionWithSenderAndRecipientAndAmountAndNonceAndFeeAndPayload maximum =
        minimumWithFee.payload(BytesValue.EMPTY);
    assertNotNull(maximum.build());
  }

}
