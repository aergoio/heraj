/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.Receipt;
import org.junit.Test;
import types.Blockchain;

public class ReceiptConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<Receipt, Blockchain.Receipt> converter =
        new ReceiptConverterFactory().create();

    final Blockchain.Receipt rpcAccount = Blockchain.Receipt.newBuilder().build();
    final Receipt domainReceipt = converter.convertToDomainModel(rpcAccount);
    assertNotNull(domainReceipt);
  }

}
