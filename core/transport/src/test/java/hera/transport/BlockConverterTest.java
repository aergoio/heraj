/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.util.ValidationUtils.assertNotNull;
import static java.util.Arrays.asList;

import hera.AbstractTestCase;
import hera.api.model.AccountAddress;
import hera.api.model.Block;
import hera.api.model.BytesValue;
import hera.api.model.Transaction;
import org.junit.Test;
import types.Blockchain;

public class BlockConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<Block, Blockchain.Block> converter = new BlockConverterFactory().create();

    final Block domainBlock = new Block();
    final Transaction domainTransaction = new Transaction();
    domainTransaction
        .setSender(AccountAddress.of(BytesValue.of(new byte[] {AccountAddress.VERSION})));
    domainTransaction.setRecipient(
        AccountAddress.of(BytesValue.of(new byte[] {AccountAddress.VERSION})));
    domainBlock.setTransactions(asList(domainTransaction));

    final Blockchain.Block rpcBlock = converter.convertToRpcModel(domainBlock);
    final Block actualDomainBlock = converter.convertToDomainModel(rpcBlock);
    assertNotNull(actualDomainBlock);
  }

}
