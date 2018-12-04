/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.BLOCK_GETBLOCK_BY_HASH;
import static hera.TransportConstants.BLOCK_GETBLOCK_BY_HEIGHT;
import static hera.TransportConstants.BLOCK_LIST_HEADERS_BY_HASH;
import static hera.TransportConstants.BLOCK_LIST_HEADERS_BY_HEIGHT;
import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
  public void testGetBlockByHash() {
    final BlockBaseTemplate base = mock(BlockBaseTemplate.class);
    final ResultOrErrorFuture<Block> future = ResultOrErrorFutureFactory.supply(() -> new Block());
    when(base.getBlockByHashFunction()).thenReturn((h) -> future);

    final BlockTemplate blockTemplate = supplyBlockTemplate(base);

    final Block block =
        blockTemplate.getBlock(new BlockHash(of(randomUUID().toString().getBytes())));
    assertNotNull(block);
    assertEquals(BLOCK_GETBLOCK_BY_HASH,
        ((WithIdentity) blockTemplate.getBlockByHashFunction()).getIdentity());
  }

  @Test
  public void testGetBlockByHeight() {
    final BlockBaseTemplate base = mock(BlockBaseTemplate.class);
    final ResultOrErrorFuture<Block> future = ResultOrErrorFutureFactory.supply(() -> new Block());
    when(base.getBlockByHeightFunction()).thenReturn((h) -> future);

    final BlockTemplate blockTemplate = supplyBlockTemplate(base);

    final Block block = blockTemplate.getBlock(randomUUID().hashCode());
    assertNotNull(block);
    assertEquals(BLOCK_GETBLOCK_BY_HEIGHT,
        ((WithIdentity) blockTemplate.getBlockByHeightFunction()).getIdentity());
  }

  @Test
  public void testListBlockHeadersByHash() {
    final BlockBaseTemplate base = mock(BlockBaseTemplate.class);
    final ResultOrErrorFuture<List<BlockHeader>> future =
        ResultOrErrorFutureFactory.supply(() -> new ArrayList<BlockHeader>());
    when(base.getListBlockHeadersByHashFunction()).thenReturn((h, c) -> future);

    final BlockTemplate blockTemplate = supplyBlockTemplate(base);

    final List<BlockHeader> blockHeaders = blockTemplate.listBlockHeaders(
        new BlockHash(of(randomUUID().toString().getBytes())), randomUUID().hashCode());
    assertNotNull(blockHeaders);
    assertEquals(BLOCK_LIST_HEADERS_BY_HASH,
        ((WithIdentity) blockTemplate.getListBlockHeadersByHashFunction()).getIdentity());
  }

  @Test
  public void testListBlockHeadersByHeight() {
    final BlockBaseTemplate base = mock(BlockBaseTemplate.class);
    final ResultOrErrorFuture<List<BlockHeader>> future =
        ResultOrErrorFutureFactory.supply(() -> new ArrayList<BlockHeader>());
    when(base.getListBlockHeadersByHeightFunction()).thenReturn((h, c) -> future);

    final BlockTemplate blockTemplate = supplyBlockTemplate(base);

    final List<BlockHeader> blockHeaders =
        blockTemplate.listBlockHeaders(randomUUID().hashCode(), randomUUID().hashCode());
    assertNotNull(blockHeaders);
    assertEquals(BLOCK_LIST_HEADERS_BY_HEIGHT,
        ((WithIdentity) blockTemplate.getListBlockHeadersByHeightFunction()).getIdentity());
  }

}
