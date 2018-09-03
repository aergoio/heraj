/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.api.BlockAsyncOperation;
import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockHeader;
import hera.api.tupleorerror.ResultOrError;
import hera.api.tupleorerror.ResultOrErrorFuture;
import java.util.List;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceBlockingStub;
import types.Blockchain;
import types.Rpc;

@PrepareForTest({AergoRPCServiceBlockingStub.class, Blockchain.Block.class,
    Rpc.BlockHeaderList.class})
public class BlockTemplateTest extends AbstractTestCase {

  @Test
  public void testGetBlockByHash() throws Exception {
    ResultOrErrorFuture<Block> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(ResultOrError.class));
    BlockAsyncOperation asyncOperationMock = mock(BlockAsyncOperation.class);
    when(asyncOperationMock.getBlock(any())).thenReturn(futureMock);

    final BlockTemplate blockTemplate = new BlockTemplate(asyncOperationMock);

    final ResultOrError<Block> block =
        blockTemplate.getBlock(new BlockHash(randomUUID().toString().getBytes()));
    assertNotNull(block);
  }

  @Test
  public void testGetBlockByHeight() throws Exception {
    ResultOrErrorFuture<Block> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(ResultOrError.class));
    BlockAsyncOperation asyncOperationMock = mock(BlockAsyncOperation.class);
    when(asyncOperationMock.getBlock(anyLong())).thenReturn(futureMock);

    final BlockTemplate blockTemplate = new BlockTemplate(asyncOperationMock);

    final ResultOrError<Block> block = blockTemplate.getBlock(randomUUID().hashCode());
    assertNotNull(block);
  }

  @Test
  public void testListBlockHeadersByHash() throws Exception {
    ResultOrErrorFuture<List<BlockHeader>> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(ResultOrError.class));
    BlockAsyncOperation asyncOperationMock = mock(BlockAsyncOperation.class);
    when(asyncOperationMock.listBlockHeaders(any(), anyInt())).thenReturn(futureMock);

    final BlockTemplate blockTemplate = new BlockTemplate(asyncOperationMock);

    final ResultOrError<List<BlockHeader>> block = blockTemplate.listBlockHeaders(
        new BlockHash(randomUUID().toString().getBytes()), randomUUID().hashCode());
    assertNotNull(block);
  }

  @Test
  public void testListBlockHeadersByHeight() throws Exception {
    ResultOrErrorFuture<List<BlockHeader>> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(ResultOrError.class));
    BlockAsyncOperation asyncOperationMock = mock(BlockAsyncOperation.class);
    when(asyncOperationMock.listBlockHeaders(anyLong(), anyInt())).thenReturn(futureMock);

    final BlockTemplate blockTemplate = new BlockTemplate(asyncOperationMock);

    final ResultOrError<List<BlockHeader>> block =
        blockTemplate.listBlockHeaders(randomUUID().hashCode(), randomUUID().hashCode());
    assertNotNull(block);
  }
}
