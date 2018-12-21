/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;

import hera.AbstractTestCase;
import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BytesValue;
import hera.api.model.Fee;
import hera.api.model.RawTransaction;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import org.junit.Test;
import types.Blockchain;

public class BlockConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<Block, Blockchain.Block> converter = new BlockConverterFactory().create();

    final BlockHash blockHash = BlockHash.of(BytesValue.of(randomUUID().toString().getBytes()));

    final RawTransaction rawTransaction = Transaction.newBuilder()
        .from(AccountAddress.of(BytesValue.EMPTY))
        .to(AccountAddress.of(BytesValue.EMPTY))
        .amount("10000", Unit.AER)
        .nonce(1L)
        .fee(Fee.of(Aer.of("100", Unit.AER), 5))
        .payload(BytesValue.EMPTY)
        .build();

    final Transaction domainTransaction = new Transaction(
        rawTransaction.getSender(),
        rawTransaction.getRecipient(),
        rawTransaction.getAmount(),
        rawTransaction.getNonce(),
        rawTransaction.getFee(),
        rawTransaction.getPayload(),
        rawTransaction.getTxType(),
        Signature.of(BytesValue.EMPTY),
        TxHash.of(BytesValue.EMPTY),
        blockHash,
        0,
        true);

    final Block expected = new Block(null, blockHash, null, 0, 0, null, null, null,
        0, null, null, null, 1, asList(domainTransaction));
    final Blockchain.Block rpcBlock = converter.convertToRpcModel(expected);
    final Block actual = converter.convertToDomainModel(rpcBlock);
    assertEquals(expected, actual);
  }

}
