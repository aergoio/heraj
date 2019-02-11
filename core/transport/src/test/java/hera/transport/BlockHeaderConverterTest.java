/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static hera.util.TransportUtils.copyFrom;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;

import hera.AbstractTestCase;
import hera.api.model.BlockHash;
import hera.api.model.BlockHeader;
import hera.api.model.BytesValue;
import org.junit.Test;
import types.Rpc;
import types.Rpc.BlockMetadata;

public class BlockHeaderConverterTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<BlockHeader, Rpc.BlockMetadata> converter =
        new BlockHeaderConverterFactory().create();

    final BlockHash blockHash = BlockHash.of(BytesValue.of(randomUUID().toString().getBytes()));
    final BlockMetadata blockMetadata = Rpc.BlockMetadata.newBuilder()
        .setHash(copyFrom(blockHash.getBytesValue()))
        .setTxcount(1)
        .build();

    final BlockHeader expected = new BlockHeader(null, blockHash, null, 0, 0, null, null, null,
        0, null, null, null, 1);
    final BlockHeader actual = converter.convertToDomainModel(blockMetadata);
    assertEquals(expected, actual);
  }

}
