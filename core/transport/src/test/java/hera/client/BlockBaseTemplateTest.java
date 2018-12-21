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
public class BlockBaseTemplateTest extends AbstractTestCase {

  @Override
  public void setUp() {
    super.setUp();
  }

  protected BlockBaseTemplate supplyBlockBaseTemplate(
      final AergoRPCServiceFutureStub aergoService) {
    final BlockBaseTemplate blockBaseTemplate = new BlockBaseTemplate();
    blockBaseTemplate.aergoService = aergoService;
    blockBaseTemplate.contextProvider = () -> context;
    return blockBaseTemplate;
  }

  @Test
  public void testGetBlockByHash() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture =
        service.submit(() -> Blockchain.Block.newBuilder().build());
    when(aergoService.getBlock(any())).thenReturn(mockListenableFuture);

    final BlockBaseTemplate blockBaseTemplate = supplyBlockBaseTemplate(aergoService);

    final ResultOrErrorFuture<Block> block =
        blockBaseTemplate.getBlockByHashFunction()
            .apply(new BlockHash(of(randomUUID().toString().getBytes())));
    assertTrue(block.get().hasResult());
  }

  @Test
  public void testGetBlockByHeight() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture =
        service.submit(() -> Blockchain.Block.newBuilder().build());
    when(aergoService.getBlock(any())).thenReturn(mockListenableFuture);

    final BlockBaseTemplate blockBaseTemplate = supplyBlockBaseTemplate(aergoService);

    final ResultOrErrorFuture<Block> block =
        blockBaseTemplate.getBlockByHeightFunction().apply(10L);
    assertTrue(block.get().hasResult());
  }

  @Test
  public void testListBlockHeadersByHash() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = service.submit(() -> Rpc.BlockMetadataList.newBuilder()
        .addBlocks(Rpc.BlockMetadata.newBuilder().build()).build());
    when(aergoService.listBlockMetadata(any())).thenReturn(mockListenableFuture);

    final BlockBaseTemplate blockBaseTemplate = supplyBlockBaseTemplate(aergoService);

    final ResultOrErrorFuture<List<BlockHeader>> blockHeaders =
        blockBaseTemplate.getListBlockHeadersByHashFunction().apply(
            new BlockHash(of(randomUUID().toString().getBytes())), 10);
    assertTrue(blockHeaders.get().hasResult());
  }

  @Test
  public void testListBlockHeadersByHeight() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture =
        service.submit(() -> Rpc.BlockMetadataList.newBuilder().build());
    when(aergoService.listBlockMetadata(any())).thenReturn(mockListenableFuture);

    final BlockBaseTemplate blockBaseTemplate = supplyBlockBaseTemplate(aergoService);

    final ResultOrErrorFuture<List<BlockHeader>> blockHeaders =
        blockBaseTemplate.getListBlockHeadersByHeightFunction().apply(10L, 10);
    assertTrue(blockHeaders.get().hasResult());
  }

}
