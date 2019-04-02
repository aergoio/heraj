/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;

import hera.AbstractTestCase;
import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.BlockHash;
import hera.api.model.BytesValue;
import hera.api.model.ChainIdHash;
import hera.api.model.Fee;
import hera.api.model.RawTransaction;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import org.junit.Test;
import types.Blockchain;

public class TransactionInBlockConverterTest extends AbstractTestCase {

  protected final String encodedAddress = "AtmxbVvjDN5LYwaf5QrCZPc3FoAqUCMVegVXjf8CMCz59wL21X6j";

  @Test
  public void testConvert() {
    final ModelConverter<Transaction, Blockchain.TxInBlock> converter =
        new TransactionInBlockConverterFactory().create();
    final RawTransaction rawTransaction =
        Transaction.newBuilder(new ChainIdHash(of(randomUUID().toString().getBytes())))
            .from(AccountAddress.of(encodedAddress))
            .to(AccountAddress.of(encodedAddress))
            .amount("0.001", Unit.AERGO)
            .nonce(1L)
            .fee(Fee.of(Aer.of("100", Unit.AER), 5))
            .payload(BytesValue.EMPTY)
            .build();

    final Transaction expected = new Transaction(
        rawTransaction.getChainIdHash(),
        rawTransaction.getSender(),
        rawTransaction.getRecipient(),
        rawTransaction.getAmount(),
        rawTransaction.getNonce(),
        rawTransaction.getFee(),
        rawTransaction.getPayload(),
        rawTransaction.getTxType(),
        Signature.of(BytesValue.EMPTY),
        TxHash.of(BytesValue.EMPTY),
        BlockHash.of(BytesValue.of(randomUUID().toString().getBytes())),
        1,
        true);

    final Blockchain.TxInBlock rpcTransaction = converter.convertToRpcModel(expected);
    final Transaction actual = converter.convertToDomainModel(rpcTransaction);
    assertEquals(expected, actual);
  }

}
