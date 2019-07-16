/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.junit.Assert.assertNotNull;

import com.google.protobuf.ByteString;
import hera.AbstractTestCase;
import hera.api.model.ContractTxReceipt;
import hera.spec.resolver.AddressSpec;
import org.junit.Test;
import types.Blockchain;

public class ReceiptConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<ContractTxReceipt, Blockchain.Receipt> converter =
        new ReceiptConverterFactory().create();

    final Blockchain.Receipt rpcAccount = Blockchain.Receipt.newBuilder()
        .setContractAddress(ByteString.copyFrom(new byte[] {AddressSpec.PREFIX}))
        .build();
    final ContractTxReceipt domainReceipt = converter.convertToDomainModel(rpcAccount);
    assertNotNull(domainReceipt);
  }

}
