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
import hera.api.model.BlockHeader;
import hera.api.model.Hash;
import hera.transport.ModelConverter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceBlockingStub;
import types.Blockchain;
import types.Rpc;

@PrepareForTest({AergoRPCServiceBlockingStub.class, Blockchain.Block.class,
    Rpc.BlockHeaderList.class})
public class BlockTemplateTest extends AbstractTestCase {

  @Test
  public void testGetBlock() throws Exception {
    CompletableFuture<Block> futureMock = mock(CompletableFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(Block.class));
    BlockAsyncOperation asyncOperationMock = mock(BlockAsyncOperation.class);
    when(asyncOperationMock.getBlock(any())).thenReturn(futureMock);

    final BlockTemplate blockTemplate = new BlockTemplate(asyncOperationMock);

    final Block block = blockTemplate.getBlock(new Hash(randomUUID().toString().getBytes()));
    assertNotNull(block);
  }

  @Test
  public void testListBlockHeadersByHash() throws Exception {
    CompletableFuture<List<BlockHeader>> futureMock = mock(CompletableFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(List.class));
    BlockAsyncOperation asyncOperationMock = mock(BlockAsyncOperation.class);
    when(asyncOperationMock.listBlockHeaders(any(Hash.class), anyInt())).thenReturn(futureMock);

    final BlockTemplate blockTemplate = new BlockTemplate(asyncOperationMock);

    final List<BlockHeader> block = blockTemplate
        .listBlockHeaders(new Hash(randomUUID().toString().getBytes()), randomUUID().hashCode());
    assertNotNull(block);
  }

  @Test
  public void testListBlockHeadersByHeight() throws Exception {
    CompletableFuture<List<BlockHeader>> futureMock = mock(CompletableFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(List.class));
    BlockAsyncOperation asyncOperationMock = mock(BlockAsyncOperation.class);
    when(asyncOperationMock.listBlockHeaders(anyLong(), anyInt())).thenReturn(futureMock);

    final BlockTemplate blockTemplate = new BlockTemplate(asyncOperationMock);

    final List<BlockHeader> block = blockTemplate
        .listBlockHeaders(randomUUID().hashCode(), randomUUID().hashCode());
    assertNotNull(block);
  }
}
