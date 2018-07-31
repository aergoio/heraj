/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.api.model.Block;
import hera.api.model.BlockHeader;
import hera.api.model.Hash;
import hera.transport.ModelConverter;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceBlockingStub;
import types.Blockchain;
import types.Rpc;

@PrepareForTest({AergoRPCServiceBlockingStub.class, Blockchain.Block.class,
    Rpc.BlockHeaderList.class})
public class BlockTemplateTest extends AbstractTestCase {

  protected static final ModelConverter<Block, Blockchain.Block> blockConverter = mock(
      ModelConverter.class);

  @BeforeClass
  public static void setUpBeforeClass() {
    when(blockConverter.convertToDomainModel(any(Blockchain.Block.class)))
        .thenReturn(mock(Block.class));
    when(blockConverter.convertToRpcModel(any(Block.class)))
        .thenReturn(mock(Blockchain.Block.class));
  }

  @Test
  public void testGetBlock() {
    final AergoRPCServiceBlockingStub aergoService = mock(AergoRPCServiceBlockingStub.class);
    when(aergoService.getBlock(any())).thenReturn(mock(Blockchain.Block.class));

    final BlockTemplate blockTemplate = new BlockTemplate(aergoService, blockConverter);

    final Hash hash = new Hash(randomUUID().toString().getBytes());
    final Block block = blockTemplate.getBlock(hash);
    assertNotNull(block);
  }

  @Test
  public void testListBlockHeadersByHash() {
    final AergoRPCServiceBlockingStub aergoService = mock(AergoRPCServiceBlockingStub.class);
    when(aergoService.listBlockHeaders(any())).thenReturn(mock(Rpc.BlockHeaderList.class));

    final BlockTemplate blockTemplate = new BlockTemplate(aergoService, blockConverter);

    final Hash hash = new Hash(randomUUID().toString().getBytes());
    final List<BlockHeader> blockHeaders = blockTemplate
        .listBlockHeaders(hash, randomUUID().hashCode());
    assertNotNull(blockHeaders);
  }

  @Test
  public void testListBlockHeadersByHeight() {
    final AergoRPCServiceBlockingStub aergoService = mock(AergoRPCServiceBlockingStub.class);
    when(aergoService.listBlockHeaders(any())).thenReturn(mock(Rpc.BlockHeaderList.class));

    final BlockTemplate blockTemplate = new BlockTemplate(aergoService, blockConverter);

    final List<BlockHeader> blockHeaders = blockTemplate
        .listBlockHeaders(randomUUID().hashCode(), randomUUID().hashCode());
    assertNotNull(blockHeaders);
  }
}
