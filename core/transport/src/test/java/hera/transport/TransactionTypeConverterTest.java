/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.junit.Assert.assertEquals;

import hera.AbstractTestCase;
import hera.api.model.Transaction;
import hera.api.model.Transaction.TxType;
import org.junit.Test;
import types.Blockchain;

public class TransactionTypeConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<Transaction.TxType, Blockchain.TxType> converter =
        new TransactionTypeConverterFactory().create();
    // final Blockchain.TxType[] rpcTxtypes = Blockchain.TxType.values();
    for (final TxType expected : Transaction.TxType.values()) {
      final Blockchain.TxType rpcTxType = converter.convertToRpcModel(expected);
      final TxType actual = converter.convertToDomainModel(rpcTxType);
      assertEquals(expected, actual);
    }
  }

}
