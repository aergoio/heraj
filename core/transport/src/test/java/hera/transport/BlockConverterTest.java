/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.Block;
import org.junit.Test;
import types.Blockchain;

public class BlockConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<Block, Blockchain.Block> converter = new BlockConverterFactory().create();
    final Blockchain.Block rpcBlock = Blockchain.Block.newBuilder().build();
    final Block converted = converter.convertToDomainModel(rpcBlock);
    assertNotNull(converted);
  }

}
