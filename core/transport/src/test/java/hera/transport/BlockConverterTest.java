/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.util.ValidationUtils.assertNotNull;
import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;

import hera.AbstractTestCase;
import hera.api.model.AccountAddress;
import hera.api.model.Block;
import hera.api.model.Hash;
import hera.api.model.Transaction;
import org.junit.Test;
import types.Blockchain;

public class BlockConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<Block, Blockchain.Block> converter = new BlockConverterFactory()
        .create();

    final Block domainBlock = new Block();
    domainBlock.setHash(new Hash(randomUUID().toString().getBytes()));
    domainBlock.setPreviousBlockHash(new Hash(randomUUID().toString().getBytes()));
    domainBlock.setRootHash(new Hash(randomUUID().toString().getBytes()));
    domainBlock.setTransactionsRootHash(new Hash(randomUUID().toString().getBytes()));
    domainBlock.setPublicKey(new Hash(randomUUID().toString().getBytes()));
    domainBlock.setSign(new Hash(randomUUID().toString().getBytes()));

    final Transaction domainTransaction = new Transaction();
    domainTransaction.setSender(AccountAddress.of(randomUUID().toString().getBytes()));
    domainTransaction.setRecipient(AccountAddress.of(randomUUID().toString().getBytes()));
    domainBlock.setTransactions(asList(domainTransaction));

    final Blockchain.Block rpcBlock = converter.convertToRpcModel(domainBlock);
    final Block actualDomainBlock = converter.convertToDomainModel(rpcBlock);
    assertNotNull(actualDomainBlock);
  }

}
