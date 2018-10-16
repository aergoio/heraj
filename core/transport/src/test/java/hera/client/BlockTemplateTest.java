/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockHeader;
import hera.api.tupleorerror.ResultOrError;
import java.util.List;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceBlockingStub;
import types.Blockchain;
import types.Rpc;

@SuppressWarnings("unchecked")
@PrepareForTest({AergoRPCServiceBlockingStub.class, Blockchain.Block.class,
    Rpc.BlockHeaderList.class})
public class BlockTemplateTest extends AbstractTestCase {

  @Test
  public void testGetBlockByHash() throws Exception {
    ResultOrError<Block> eitherMock = mock(ResultOrError.class);
    when(eitherMock.getResult()).thenReturn(mock(Block.class));
    BlockEitherTemplate eitherOperationMock = mock(BlockEitherTemplate.class);
    when(eitherOperationMock.getBlock(any())).thenReturn(eitherMock);

    final BlockTemplate blockTemplate = new BlockTemplate();
    blockTemplate.blockEitherOperation = eitherOperationMock;

    final Block block =
        blockTemplate.getBlock(new BlockHash(of(randomUUID().toString().getBytes())));
    assertNotNull(block);
  }

  @Test
  public void testGetBlockByHeight() throws Exception {
    ResultOrError<Block> eitherMock = mock(ResultOrError.class);
    when(eitherMock.getResult()).thenReturn(mock(Block.class));
    BlockEitherTemplate eitherOperationMock = mock(BlockEitherTemplate.class);
    when(eitherOperationMock.getBlock(anyLong())).thenReturn(eitherMock);

    final BlockTemplate blockTemplate = new BlockTemplate();
    blockTemplate.blockEitherOperation = eitherOperationMock;

    final Block block = blockTemplate.getBlock(randomUUID().hashCode());
    assertNotNull(block);
  }

  @Test
  public void testListBlockHeadersByHash() throws Exception {
    ResultOrError<List<BlockHeader>> eitherMock = mock(ResultOrError.class);
    when(eitherMock.getResult()).thenReturn(mock(List.class));
    BlockEitherTemplate eitherOperationMock = mock(BlockEitherTemplate.class);
    when(eitherOperationMock.listBlockHeaders(any(), anyInt())).thenReturn(eitherMock);

    final BlockTemplate blockTemplate = new BlockTemplate();
    blockTemplate.blockEitherOperation = eitherOperationMock;

    final List<BlockHeader> block = blockTemplate.listBlockHeaders(
        new BlockHash(of(randomUUID().toString().getBytes())), randomUUID().hashCode());
    assertNotNull(block);
  }

  @Test
  public void testListBlockHeadersByHeight() throws Exception {
    ResultOrError<List<BlockHeader>> eitherMock = mock(ResultOrError.class);
    when(eitherMock.getResult()).thenReturn(mock(List.class));
    BlockEitherTemplate eitherOperationMock = mock(BlockEitherTemplate.class);
    when(eitherOperationMock.listBlockHeaders(anyLong(), anyInt())).thenReturn(eitherMock);

    final BlockTemplate blockTemplate = new BlockTemplate();
    blockTemplate.blockEitherOperation = eitherOperationMock;

    final List<BlockHeader> block =
        blockTemplate.listBlockHeaders(randomUUID().hashCode(), randomUUID().hashCode());
    assertNotNull(block);
  }

}
