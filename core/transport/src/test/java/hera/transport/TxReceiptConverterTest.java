/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.util.TransportUtils.copyFrom;
import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.ContractTxReceipt;
import org.junit.Test;
import types.Blockchain;

public class TxReceiptConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<ContractTxReceipt, Blockchain.Receipt> converter =
        new TxReceiptConverterFactory().create();

    final Blockchain.Receipt rpcAccount = Blockchain.Receipt.newBuilder()
        .setContractAddress(copyFrom(accountAddress.getBytesValue()))
        .build();
    final ContractTxReceipt domainReceipt = converter.convertToDomainModel(rpcAccount);
    assertNotNull(domainReceipt);
  }

}
