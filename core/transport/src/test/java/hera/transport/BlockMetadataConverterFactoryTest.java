/*
 * @copyright defined in LICENSE.txt
 */

package hera.transport;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.BlockMetadata;
import org.junit.Test;
import types.Rpc;

public class BlockMetadataConverterFactoryTest extends AbstractTestCase {

  @Test
  public void testConvert() {
    final ModelConverter<BlockMetadata, Rpc.BlockMetadata> converter =
        new BlockMetadataConverterFactory().create();
    final Rpc.BlockMetadata rpcBlockMetadata = Rpc.BlockMetadata.newBuilder().build();
    final BlockMetadata converted = converter.convertToDomainModel(rpcBlockMetadata);
    assertNotNull(converted);
  }

}
