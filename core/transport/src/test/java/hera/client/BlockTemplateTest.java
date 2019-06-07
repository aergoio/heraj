/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.BLOCK_GET_BLOCK_BY_HASH;
import static hera.TransportConstants.BLOCK_GET_BLOCK_BY_HEIGHT;
import static hera.TransportConstants.BLOCK_GET_METADATA_BY_HASH;
import static hera.TransportConstants.BLOCK_GET_METADATA_BY_HEIGHT;
import static hera.TransportConstants.BLOCK_LIST_METADATAS_BY_HASH;
import static hera.TransportConstants.BLOCK_LIST_METADATAS_BY_HEIGHT;
import static hera.TransportConstants.BLOCK_SUBSCRIBE_BLOCK;
import static hera.TransportConstants.BLOCK_SUBSCRIBE_BLOCKMETADATA;
import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.ContextProvider;
import hera.api.function.Function1;
import hera.api.function.Function2;
import hera.api.function.WithIdentity;
import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockMetadata;
import hera.api.model.StreamObserver;
import hera.api.model.Subscription;
import hera.client.internal.BlockBaseTemplate;
import hera.client.internal.FinishableFuture;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;

@PrepareForTest({AergoRPCServiceFutureStub.class})
public class BlockTemplateTest extends AbstractTestCase {

  @Override
  public void setUp() {
    super.setUp();
  }

  protected BlockTemplate supplyBlockTemplate(
      final BlockBaseTemplate blockBaseTemplate) {
    final BlockTemplate blockTemplate = new BlockTemplate();
    blockTemplate.blockBaseTemplate = blockBaseTemplate;
    blockTemplate.setContextProvider(ContextProvider.defaultProvider);
    return blockTemplate;
  }

  @Test
  public void testGetBlockMetadataByHash() {
    final BlockBaseTemplate base = mock(BlockBaseTemplate.class);
    final BlockMetadata mockBlockMetadata = mock(BlockMetadata.class);
    final FinishableFuture<BlockMetadata> future = new FinishableFuture<BlockMetadata>();
    future.success(mockBlockMetadata);
    when(base.getBlockMetatdataByHashFunction())
        .thenReturn(new Function1<BlockHash, FinishableFuture<BlockMetadata>>() {
          @Override
          public FinishableFuture<BlockMetadata> apply(BlockHash t) {
            return future;
          }
        });

    final BlockTemplate blockTemplate = supplyBlockTemplate(base);

    final BlockMetadata blockMetadata =
        blockTemplate.getBlockMetadata(new BlockHash(of(randomUUID().toString().getBytes())));
    assertNotNull(blockMetadata);
    assertEquals(BLOCK_GET_METADATA_BY_HASH,
        ((WithIdentity) blockTemplate.getBlockMetadataByHashFunction()).getIdentity());
  }

  @Test
  public void testGetBlockMetadataByHeight() {
    final BlockBaseTemplate base = mock(BlockBaseTemplate.class);
    final BlockMetadata mockBlockMetadata = mock(BlockMetadata.class);
    final FinishableFuture<BlockMetadata> future = new FinishableFuture<BlockMetadata>();
    future.success(mockBlockMetadata);
    when(base.getBlockMetadataByHeightFunction())
        .thenReturn(new Function1<Long, FinishableFuture<BlockMetadata>>() {
          @Override
          public FinishableFuture<BlockMetadata> apply(Long t) {
            return future;
          }
        });

    final BlockTemplate blockTemplate = supplyBlockTemplate(base);

    final BlockMetadata blockMetadata =
        blockTemplate.getBlockMetadata(randomUUID().hashCode());
    assertNotNull(blockMetadata);
    assertEquals(BLOCK_GET_METADATA_BY_HEIGHT,
        ((WithIdentity) blockTemplate.getBlockMetadataByHeightFunction()).getIdentity());
  }

  @Test
  public void testListBlockMetadatasByHash() {
    final BlockBaseTemplate base = mock(BlockBaseTemplate.class);
    final FinishableFuture<List<BlockMetadata>> future =
        new FinishableFuture<List<BlockMetadata>>();
    future.success(new ArrayList<BlockMetadata>());
    when(base.getListBlockMetadatasByHashFunction())
        .thenReturn(new Function2<BlockHash, Integer, FinishableFuture<List<BlockMetadata>>>() {
          @Override
          public FinishableFuture<List<BlockMetadata>> apply(BlockHash t1, Integer t2) {
            return future;
          }
        });

    final BlockTemplate blockTemplate = supplyBlockTemplate(base);

    final List<BlockMetadata> blockMetadatas = blockTemplate.listBlockMetadatas(
        new BlockHash(of(randomUUID().toString().getBytes())), randomUUID().hashCode());
    assertNotNull(blockMetadatas);
    assertEquals(BLOCK_LIST_METADATAS_BY_HASH,
        ((WithIdentity) blockTemplate.getListBlockMetadatasByHashFunction()).getIdentity());
  }

  @Test
  public void testListBlockMetadatasByHeight() {
    final BlockBaseTemplate base = mock(BlockBaseTemplate.class);
    final FinishableFuture<List<BlockMetadata>> future =
        new FinishableFuture<List<BlockMetadata>>();
    future.success(new ArrayList<BlockMetadata>());
    when(base.getListBlockMetadatasByHeightFunction())
        .thenReturn(new Function2<Long, Integer, FinishableFuture<List<BlockMetadata>>>() {
          @Override
          public FinishableFuture<List<BlockMetadata>> apply(Long t1, Integer t2) {
            return future;
          }
        });

    final BlockTemplate blockTemplate = supplyBlockTemplate(base);

    final List<BlockMetadata> blockMetadatas =
        blockTemplate.listBlockMetadatas(randomUUID().hashCode(), randomUUID().hashCode());
    assertNotNull(blockMetadatas);
    assertEquals(BLOCK_LIST_METADATAS_BY_HEIGHT,
        ((WithIdentity) blockTemplate.getListBlockMetadatasByHeightFunction()).getIdentity());
  }

  @Test
  public void testGetBlockByHash() {
    final BlockBaseTemplate base = mock(BlockBaseTemplate.class);
    final Block mockBlock = mock(Block.class);
    final FinishableFuture<Block> future = new FinishableFuture<Block>();
    future.success(mockBlock);
    when(base.getBlockByHashFunction())
        .thenReturn(new Function1<BlockHash, FinishableFuture<Block>>() {
          @Override
          public FinishableFuture<Block> apply(BlockHash t) {
            return future;
          }
        });

    final BlockTemplate blockTemplate = supplyBlockTemplate(base);

    final Block block =
        blockTemplate.getBlock(new BlockHash(of(randomUUID().toString().getBytes())));
    assertNotNull(block);
    assertEquals(BLOCK_GET_BLOCK_BY_HASH,
        ((WithIdentity) blockTemplate.getBlockByHashFunction()).getIdentity());
  }

  @Test
  public void testGetBlockByHeight() {
    final BlockBaseTemplate base = mock(BlockBaseTemplate.class);
    final Block mockBlock = mock(Block.class);
    final FinishableFuture<Block> future = new FinishableFuture<Block>();
    future.success(mockBlock);
    when(base.getBlockByHeightFunction())
        .thenReturn(new Function1<Long, FinishableFuture<Block>>() {
          @Override
          public FinishableFuture<Block> apply(Long t) {
            return future;
          }
        });

    final BlockTemplate blockTemplate = supplyBlockTemplate(base);

    final Block block = blockTemplate.getBlock(randomUUID().hashCode());
    assertNotNull(block);
    assertEquals(BLOCK_GET_BLOCK_BY_HEIGHT,
        ((WithIdentity) blockTemplate.getBlockByHeightFunction()).getIdentity());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testSubscribeBlockMetadata() {
    final BlockBaseTemplate base = mock(BlockBaseTemplate.class);
    final Subscription<BlockMetadata> mockSubscription = mock(Subscription.class);
    when(base.getSubscribeBlockMetadataFunction())
        .thenReturn(new Function1<StreamObserver<BlockMetadata>, Subscription<BlockMetadata>>() {

          @Override
          public Subscription<BlockMetadata> apply(StreamObserver<BlockMetadata> t2) {
            return mockSubscription;
          }
        });

    final BlockTemplate blockTemplate = supplyBlockTemplate(base);

    final Subscription<BlockMetadata> subscription = blockTemplate.subscribeNewBlockMetadata(null);
    assertNotNull(subscription);
    assertEquals(BLOCK_SUBSCRIBE_BLOCKMETADATA,
        ((WithIdentity) blockTemplate.getSubscribeBlockMetadataFunction()).getIdentity());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testSubscribeBlock() {
    final BlockBaseTemplate base = mock(BlockBaseTemplate.class);
    final Subscription<Block> mockSubscription = mock(Subscription.class);
    when(base.getSubscribeBlockFunction())
        .thenReturn(new Function1<StreamObserver<Block>, Subscription<Block>>() {

          @Override
          public Subscription<Block> apply(StreamObserver<Block> t2) {
            return mockSubscription;
          }
        });

    final BlockTemplate blockTemplate = supplyBlockTemplate(base);

    final Subscription<Block> subscription = blockTemplate.subscribeNewBlock(null);
    assertNotNull(subscription);
    assertEquals(BLOCK_SUBSCRIBE_BLOCK,
        ((WithIdentity) blockTemplate.getSubscribeBlockFunction()).getIdentity());
  }

}
