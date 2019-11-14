/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.junit.Assert.assertEquals;

import hera.AbstractTestCase;
import hera.api.model.Transaction;
import org.junit.Test;
import types.Blockchain;

public class TransactionTypeConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<Transaction.TxType, Blockchain.TxType> converter =
        new TransactionTypeConverterFactory().create();
    for (final Blockchain.TxType expected : Blockchain.TxType.values()) {
      final Transaction.TxType domainTxType = converter.convertToDomainModel(expected);
      final Blockchain.TxType actual = converter.convertToRpcModel(domainTxType);
      assertEquals(expected, actual);
    }
  }

}
