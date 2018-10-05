/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.api.model.Transaction;
import org.junit.Test;
import types.Blockchain;

public class TransactionConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<Transaction, Blockchain.Tx> converter =
        new TransactionConverterFactory().create();

    final Transaction domainTransaction = new Transaction();
    domainTransaction
        .setSender(AccountAddress.of(BytesValue.of(new byte[] {AccountAddress.VERSION})));
    domainTransaction.setRecipient(
        AccountAddress.of(BytesValue.of(new byte[] {AccountAddress.VERSION})));
    final Blockchain.Tx rpcTransaction = converter.convertToRpcModel(domainTransaction);
    final Transaction actualDomainTransaction = converter.convertToDomainModel(rpcTransaction);
    assertNotNull(actualDomainTransaction);
  }

}
