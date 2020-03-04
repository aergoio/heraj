/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;

import hera.AbstractTestCase;
import hera.api.model.Aer.Unit;
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

  @Test
  public void testConvert() {
    final ModelConverter<Transaction, Blockchain.TxInBlock> converter =
        new TransactionInBlockConverterFactory().create();
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .chainIdHash(new ChainIdHash(of(randomUUID().toString().getBytes())))
        .from(accountAddress)
        .to(accountAddress)
        .amount("0.001", Unit.AERGO)
        .nonce(1L)
        .fee(Fee.of(5))
        .payload(BytesValue.EMPTY)
        .build();

    final Transaction expected = Transaction.newBuilder()
        .rawTransaction(rawTransaction)
        .signature(Signature.EMPTY)
        .hash(TxHash.of(BytesValue.EMPTY))
        .build();

    final Blockchain.TxInBlock rpcTransaction = converter.convertToRpcModel(expected);
    final Transaction actual = converter.convertToDomainModel(rpcTransaction);
    assertEquals(expected, actual);
  }

}
