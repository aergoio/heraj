/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.util.concurrent.ListenableFuture;
import hera.AbstractTestCase;
import hera.api.model.Block;
import hera.api.model.BlockHeader;
import hera.api.model.Hash;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.transport.ModelConverter;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain;
import types.Rpc;

@PrepareForTest({AergoRPCServiceFutureStub.class, Blockchain.Block.class,
    Rpc.BlockHeaderList.class})
public class BlockAsyncTemplateTest extends AbstractTestCase {

  protected static final ModelConverter<Block, Blockchain.Block> blockConverter =
      mock(ModelConverter.class);

  @BeforeClass
  public static void setUpBeforeClass() {
    when(blockConverter.convertToDomainModel(any(Blockchain.Block.class)))
        .thenReturn(mock(Block.class));
    when(blockConverter.convertToRpcModel(any(Block.class)))
        .thenReturn(mock(Blockchain.Block.class));
  }

  @Test
  public void testGetBlockByHash() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = mock(ListenableFuture.class);
    when(aergoService.getBlock(any())).thenReturn(mockListenableFuture);

    final BlockAsyncTemplate blockAsyncTemplate =
        new BlockAsyncTemplate(aergoService, blockConverter);

    final ResultOrErrorFuture<Block> block =
        blockAsyncTemplate.getBlock(new Hash(randomUUID().toString().getBytes()));
    assertNotNull(block);
  }

  @Test
  public void testGetBlockByHeight() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = mock(ListenableFuture.class);
    when(aergoService.getBlock(any())).thenReturn(mockListenableFuture);

    final BlockAsyncTemplate blockAsyncTemplate =
        new BlockAsyncTemplate(aergoService, blockConverter);

    final ResultOrErrorFuture<Block> block = blockAsyncTemplate.getBlock(randomUUID().hashCode());
    assertNotNull(block);
  }

  @Test
  public void testListBlockHeadersByHash() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = mock(ListenableFuture.class);
    when(aergoService.listBlockHeaders(any())).thenReturn(mockListenableFuture);

    final BlockAsyncTemplate blockAsyncTemplate =
        new BlockAsyncTemplate(aergoService, blockConverter);

    final ResultOrErrorFuture<List<BlockHeader>> blockHeaders = blockAsyncTemplate
        .listBlockHeaders(new Hash(randomUUID().toString().getBytes()), randomUUID().hashCode());
    assertNotNull(blockHeaders);
  }

  @Test
  public void testListBlockHeadersByHeight() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = mock(ListenableFuture.class);
    when(aergoService.listBlockHeaders(any())).thenReturn(mockListenableFuture);

    final BlockAsyncTemplate blockAsyncTemplate =
        new BlockAsyncTemplate(aergoService, blockConverter);

    final ResultOrErrorFuture<List<BlockHeader>> blockHeaders =
        blockAsyncTemplate.listBlockHeaders(randomUUID().hashCode(), randomUUID().hashCode());
    assertNotNull(blockHeaders);
  }
}
