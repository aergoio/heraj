/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.util.concurrent.ListenableFuture;
import hera.AbstractTestCase;
import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockHeader;
import hera.api.tupleorerror.ResultOrErrorFuture;
import java.util.List;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain;
import types.Rpc;

@SuppressWarnings({"unchecked", "rawtypes"})
@PrepareForTest({AergoRPCServiceFutureStub.class})
public class BlockAsyncTemplateTest extends AbstractTestCase {

  @Override
  public void setUp() {
    super.setUp();
  }

  protected BlockAsyncTemplate supplyBlockAsyncTemplate(
      final AergoRPCServiceFutureStub aergoService) {
    final BlockAsyncTemplate blockAsyncTemplate = new BlockAsyncTemplate();
    blockAsyncTemplate.setContext(context);
    blockAsyncTemplate.aergoService = aergoService;
    return blockAsyncTemplate;
  }

  @Test
  public void testGetBlockByHash() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture =
        service.submit(() -> Blockchain.Block.newBuilder().build());
    when(aergoService.getBlock(any())).thenReturn(mockListenableFuture);

    final BlockAsyncTemplate blockAsyncTemplate = supplyBlockAsyncTemplate(aergoService);

    final ResultOrErrorFuture<Block> block =
        blockAsyncTemplate.getBlock(new BlockHash(of(randomUUID().toString().getBytes())));
    assertTrue(block.get().hasResult());
  }

  @Test
  public void testGetBlockByHeight() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture =
        service.submit(() -> Blockchain.Block.newBuilder().build());
    when(aergoService.getBlock(any())).thenReturn(mockListenableFuture);

    final BlockAsyncTemplate blockAsyncTemplate = supplyBlockAsyncTemplate(aergoService);

    final ResultOrErrorFuture<Block> block = blockAsyncTemplate.getBlock(randomUUID().hashCode());
    assertTrue(block.get().hasResult());
  }

  @Test
  public void testListBlockHeadersByHash() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture =
        service.submit(() -> Rpc.BlockHeaderList.newBuilder().build());
    when(aergoService.listBlockHeaders(any())).thenReturn(mockListenableFuture);

    final BlockAsyncTemplate blockAsyncTemplate = supplyBlockAsyncTemplate(aergoService);

    final ResultOrErrorFuture<List<BlockHeader>> blockHeaders = blockAsyncTemplate.listBlockHeaders(
        new BlockHash(of(randomUUID().toString().getBytes())), randomUUID().hashCode());
    assertTrue(blockHeaders.get().hasResult());
  }

  @Test
  public void testListBlockHeadersByHeight() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture =
        service.submit(() -> Rpc.BlockHeaderList.newBuilder().build());
    when(aergoService.listBlockHeaders(any())).thenReturn(mockListenableFuture);

    final BlockAsyncTemplate blockAsyncTemplate = supplyBlockAsyncTemplate(aergoService);

    final ResultOrErrorFuture<List<BlockHeader>> blockHeaders =
        blockAsyncTemplate.listBlockHeaders(randomUUID().hashCode(), randomUUID().hashCode());
    assertTrue(blockHeaders.get().hasResult());
  }
}
