/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.junit.Assert.assertEquals;

import hera.AbstractTestCase;
import hera.api.encode.Base58WithCheckSum;
import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.BlockHash;
import hera.api.model.BytesValue;
import hera.api.model.Fee;
import hera.api.model.RawTransaction;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import org.junit.Test;
import types.Blockchain;

public class TransactionConverterTest extends AbstractTestCase {

  protected static final Base58WithCheckSum base58WithCheckSum =
      () -> "AtmxbVvjDN5LYwaf5QrCZPc3FoAqUCMVegVXjf8CMCz59wL21X6j";

  @Test
  public void testConvert() {
    final ModelConverter<Transaction, Blockchain.Tx> converter =
        new TransactionConverterFactory().create();

    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .sender(AccountAddress.of(base58WithCheckSum))
        .recipient(AccountAddress.of(base58WithCheckSum))
        .amount("10000", Unit.GAER)
        .nonce(1L)
        .fee(Fee.of(Aer.of("100", Unit.GAER), 5))
        .payload(BytesValue.EMPTY)
        .build();

    final Transaction expected = new Transaction(
        rawTransaction.getSender(),
        rawTransaction.getRecipient(),
        rawTransaction.getAmount(),
        rawTransaction.getNonce(),
        rawTransaction.getFee(),
        rawTransaction.getPayload(),
        rawTransaction.getTxType(),
        Signature.of(BytesValue.EMPTY),
        TxHash.of(BytesValue.EMPTY),
        BlockHash.of(BytesValue.EMPTY),
        0,
        false);

    final Blockchain.Tx rpcTransaction = converter.convertToRpcModel(expected);
    final Transaction actual = converter.convertToDomainModel(rpcTransaction);
    assertEquals(expected, actual);
  }

}
