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

    final RawTransaction rawTransaction = Transaction.newBuilder()
        .sender(AccountAddress.of(BytesValue.EMPTY))
        .recipient(AccountAddress.of(BytesValue.EMPTY))
        .amount("10000")
        .nonce(1L)
        .fee(Fee.of("100", 5))
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
        BlockHash.of(BytesValue.of(randomUUID().toString().getBytes())),
        1,
        true);

    final Block expected = new Block();
    expected.setTransactions(asList(domainTransaction));
    final Blockchain.Block rpcBlock = converter.convertToRpcModel(expected);
    final Block actual = converter.convertToDomainModel(rpcBlock);
    assertNotNull(actual);
  }

}
