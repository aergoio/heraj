/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.util.concurrent.ListenableFuture;
import hera.AbstractTestCase;
import hera.Context;
import hera.ContextProvider;
import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockHeader;
import java.util.List;
import java.util.concurrent.Callable;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain;
import types.Rpc;

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
    blockBaseTemplate.contextProvider = new ContextProvider() {
      @Override
      public Context get() {
        return context;
      }
    };
    return blockBaseTemplate;
  }

  @Test
  public void testGetBlockByHash() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<Blockchain.Block> mockListenableFuture =
        service.submit(new Callable<Blockchain.Block>() {
          @Override
          public Blockchain.Block call() throws Exception {
            return Blockchain.Block.newBuilder().build();
          }
        });
    when(aergoService.getBlock(any(Rpc.SingleBytes.class))).thenReturn(mockListenableFuture);

    final BlockBaseTemplate blockBaseTemplate = supplyBlockBaseTemplate(aergoService);

    final FinishableFuture<Block> block =
        blockBaseTemplate.getBlockByHashFunction()
            .apply(new BlockHash(of(randomUUID().toString().getBytes())));
    assertNotNull(block.get());
  }

  @Test
  public void testGetBlockByHeight() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<Blockchain.Block> mockListenableFuture =
        service.submit(new Callable<Blockchain.Block>() {
          @Override
          public Blockchain.Block call() throws Exception {
            return Blockchain.Block.newBuilder().build();
          }
        });
    when(aergoService.getBlock(any(Rpc.SingleBytes.class))).thenReturn(mockListenableFuture);

    final BlockBaseTemplate blockBaseTemplate = supplyBlockBaseTemplate(aergoService);

    final FinishableFuture<Block> block =
        blockBaseTemplate.getBlockByHeightFunction().apply(10L);
    assertNotNull(block.get());
  }

  @Test
  public void testListBlockHeadersByHash() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<Rpc.BlockMetadataList> mockListenableFuture =
        service.submit(new Callable<Rpc.BlockMetadataList>() {
          @Override
          public Rpc.BlockMetadataList call() throws Exception {
            return Rpc.BlockMetadataList.newBuilder()
                .addBlocks(Rpc.BlockMetadata.newBuilder().build()).build();
          }
        });
    when(aergoService.listBlockMetadata(any(Rpc.ListParams.class)))
        .thenReturn(mockListenableFuture);

    final BlockBaseTemplate blockBaseTemplate = supplyBlockBaseTemplate(aergoService);

    final FinishableFuture<List<BlockHeader>> blockHeaders =
        blockBaseTemplate.getListBlockHeadersByHashFunction().apply(
            new BlockHash(of(randomUUID().toString().getBytes())), 10);
    assertNotNull(blockHeaders.get());
  }

  @Test
  public void testListBlockHeadersByHeight() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<Rpc.BlockMetadataList> mockListenableFuture =
        service.submit(new Callable<Rpc.BlockMetadataList>() {
          @Override
          public Rpc.BlockMetadataList call() throws Exception {
            return Rpc.BlockMetadataList.newBuilder()
                .addBlocks(Rpc.BlockMetadata.newBuilder().build()).build();
          }
        });
    when(aergoService.listBlockMetadata(any(Rpc.ListParams.class)))
        .thenReturn(mockListenableFuture);

    final BlockBaseTemplate blockBaseTemplate = supplyBlockBaseTemplate(aergoService);

    final FinishableFuture<List<BlockHeader>> blockHeaders =
        blockBaseTemplate.getListBlockHeadersByHeightFunction().apply(10L, 10);
    assertNotNull(blockHeaders.get());
  }

}
