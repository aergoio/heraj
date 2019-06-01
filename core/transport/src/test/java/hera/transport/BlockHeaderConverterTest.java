/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.BlockHeader;
import org.junit.Test;
import types.Blockchain;

public class BlockHeaderConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<BlockHeader, Blockchain.BlockHeader> converter =
        new BlockHeaderConverterFactory().create();
    final Blockchain.BlockHeader rpcBlockHeader = Blockchain.BlockHeader.newBuilder().build();
    final BlockHeader converted = converter.convertToDomainModel(rpcBlockHeader);
    assertNotNull(converted);
  }

}
