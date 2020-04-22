/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.Block;
import org.junit.Test;
import types.Blockchain;
import types.Blockchain.BlockBody;
import types.Blockchain.Tx;

public class BlockConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<Block, Blockchain.Block> converter = new BlockConverterFactory().create();
    final Blockchain.Block rpcBlock = Blockchain.Block.newBuilder()
        .setBody(BlockBody.newBuilder()
            .addTxs(Tx.newBuilder().build())
            .build())
        .build();
    final Block converted = converter.convertToDomainModel(rpcBlock);
    assertNotNull(converted);
  }

}
