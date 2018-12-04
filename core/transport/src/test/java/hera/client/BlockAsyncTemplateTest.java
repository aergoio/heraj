/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.BLOCK_GETBLOCK_BY_HASH_ASYNC;
import static hera.TransportConstants.BLOCK_GETBLOCK_BY_HEIGHT_ASYNC;
import static hera.TransportConstants.BLOCK_LIST_HEADERS_BY_HASH_ASYNC;
import static hera.TransportConstants.BLOCK_LIST_HEADERS_BY_HEIGHT_ASYNC;
import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.ContextProvider;
import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockHeader;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.api.tupleorerror.WithIdentity;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;

@PrepareForTest({AergoRPCServiceFutureStub.class})
public class BlockAsyncTemplateTest extends AbstractTestCase {

  @Override
  public void setUp() {
    super.setUp();
  }

  protected BlockAsyncTemplate supplyBlockAsyncTemplate(
      final BlockBaseTemplate blockBaseTemplate) {
    final BlockAsyncTemplate blockAsyncTemplate = new BlockAsyncTemplate();
    blockAsyncTemplate.blockBaseTemplate = blockBaseTemplate;
    blockAsyncTemplate.setContextProvider(ContextProvider.defaultProvider);
    return blockAsyncTemplate;
  }

  @Test
  public void testGetBlockByHash() {
    final BlockBaseTemplate base = mock(BlockBaseTemplate.class);
    final ResultOrErrorFuture<Block> future = ResultOrErrorFutureFactory.supply(() -> new Block());
    when(base.getBlockByHashFunction()).thenReturn((h) -> future);

    final BlockAsyncTemplate blockAsyncTemplate = supplyBlockAsyncTemplate(base);

    final ResultOrErrorFuture<Block> block =
        blockAsyncTemplate.getBlock(new BlockHash(of(randomUUID().toString().getBytes())));
    assertTrue(block.get().hasResult());
    assertEquals(BLOCK_GETBLOCK_BY_HASH_ASYNC,
        ((WithIdentity) blockAsyncTemplate.getBlockByHashFunction()).getIdentity());
  }

  @Test
  public void testGetBlockByHeight() {
    final BlockBaseTemplate base = mock(BlockBaseTemplate.class);
    final ResultOrErrorFuture<Block> future = ResultOrErrorFutureFactory.supply(() -> new Block());
    when(base.getBlockByHeightFunction()).thenReturn((h) -> future);

    final BlockAsyncTemplate blockAsyncTemplate = supplyBlockAsyncTemplate(base);

    final ResultOrErrorFuture<Block> block = blockAsyncTemplate.getBlock(randomUUID().hashCode());
    assertTrue(block.get().hasResult());
    assertEquals(BLOCK_GETBLOCK_BY_HEIGHT_ASYNC,
        ((WithIdentity) blockAsyncTemplate.getBlockByHeightFunction()).getIdentity());
  }

  @Test
  public void testListBlockHeadersByHash() {
    final BlockBaseTemplate base = mock(BlockBaseTemplate.class);
    final ResultOrErrorFuture<List<BlockHeader>> future =
        ResultOrErrorFutureFactory.supply(() -> new ArrayList<BlockHeader>());
    when(base.getListBlockHeadersByHashFunction()).thenReturn((h, c) -> future);

    final BlockAsyncTemplate blockAsyncTemplate = supplyBlockAsyncTemplate(base);

    final ResultOrErrorFuture<List<BlockHeader>> blockHeaders = blockAsyncTemplate.listBlockHeaders(
        new BlockHash(of(randomUUID().toString().getBytes())), randomUUID().hashCode());
    assertTrue(blockHeaders.get().hasResult());
    assertEquals(BLOCK_LIST_HEADERS_BY_HASH_ASYNC,
        ((WithIdentity) blockAsyncTemplate.getListBlockHeadersByHashFunction()).getIdentity());
  }

  @Test
  public void testListBlockHeadersByHeight() {
    final BlockBaseTemplate base = mock(BlockBaseTemplate.class);
    final ResultOrErrorFuture<List<BlockHeader>> future =
        ResultOrErrorFutureFactory.supply(() -> new ArrayList<BlockHeader>());
    when(base.getListBlockHeadersByHeightFunction()).thenReturn((h, c) -> future);

    final BlockAsyncTemplate blockAsyncTemplate = supplyBlockAsyncTemplate(base);

    final ResultOrErrorFuture<List<BlockHeader>> blockHeaders =
        blockAsyncTemplate.listBlockHeaders(randomUUID().hashCode(), randomUUID().hashCode());
    assertTrue(blockHeaders.get().hasResult());
    assertEquals(BLOCK_LIST_HEADERS_BY_HEIGHT_ASYNC,
        ((WithIdentity) blockAsyncTemplate.getListBlockHeadersByHeightFunction()).getIdentity());
  }

}
