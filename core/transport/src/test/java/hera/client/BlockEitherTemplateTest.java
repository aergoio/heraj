/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.BLOCK_GETBLOCK_BY_HASH_EITHER;
import static hera.TransportConstants.BLOCK_GETBLOCK_BY_HEIGHT_EITHER;
import static hera.TransportConstants.BLOCK_LIST_HEADERS_BY_HASH_EITHER;
import static hera.TransportConstants.BLOCK_LIST_HEADERS_BY_HEIGHT_EITHER;
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
import hera.api.tupleorerror.ResultOrError;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.api.tupleorerror.WithIdentity;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;

@PrepareForTest({AergoRPCServiceFutureStub.class})
public class BlockEitherTemplateTest extends AbstractTestCase {

  @Override
  public void setUp() {
    super.setUp();
  }

  protected BlockEitherTemplate supplyBlockEitherTemplate(
      final BlockBaseTemplate blockBaseTemplate) {
    final BlockEitherTemplate blockEitherTemplate = new BlockEitherTemplate();
    blockEitherTemplate.blockBaseTemplate = blockBaseTemplate;
    blockEitherTemplate.setContextProvider(ContextProvider.defaultProvider);
    return blockEitherTemplate;
  }

  @Test
  public void testGetBlockByHash() {
    final BlockBaseTemplate base = mock(BlockBaseTemplate.class);
    final ResultOrErrorFuture<Block> future = ResultOrErrorFutureFactory.supply(() -> new Block());
    when(base.getBlockByHashFunction()).thenReturn((h) -> future);

    final BlockEitherTemplate blockEitherTemplate = supplyBlockEitherTemplate(base);

    final ResultOrError<Block> block =
        blockEitherTemplate.getBlock(new BlockHash(of(randomUUID().toString().getBytes())));
    assertTrue(block.hasResult());
    assertEquals(BLOCK_GETBLOCK_BY_HASH_EITHER,
        ((WithIdentity) blockEitherTemplate.getBlockByHashFunction()).getIdentity());
  }

  @Test
  public void testGetBlockByHeight() {
    final BlockBaseTemplate base = mock(BlockBaseTemplate.class);
    final ResultOrErrorFuture<Block> future = ResultOrErrorFutureFactory.supply(() -> new Block());
    when(base.getBlockByHeightFunction()).thenReturn((h) -> future);

    final BlockEitherTemplate blockEitherTemplate = supplyBlockEitherTemplate(base);

    final ResultOrError<Block> block = blockEitherTemplate.getBlock(randomUUID().hashCode());
    assertTrue(block.hasResult());
    assertEquals(BLOCK_GETBLOCK_BY_HEIGHT_EITHER,
        ((WithIdentity) blockEitherTemplate.getBlockByHeightFunction()).getIdentity());
  }

  @Test
  public void testListBlockHeadersByHash() {
    final BlockBaseTemplate base = mock(BlockBaseTemplate.class);
    final ResultOrErrorFuture<List<BlockHeader>> future =
        ResultOrErrorFutureFactory.supply(() -> new ArrayList<BlockHeader>());
    when(base.getListBlockHeadersByHashFunction()).thenReturn((h, c) -> future);

    final BlockEitherTemplate blockEitherTemplate = supplyBlockEitherTemplate(base);

    final ResultOrError<List<BlockHeader>> blockHeaders = blockEitherTemplate.listBlockHeaders(
        new BlockHash(of(randomUUID().toString().getBytes())), randomUUID().hashCode());
    assertTrue(blockHeaders.hasResult());
    assertEquals(BLOCK_LIST_HEADERS_BY_HASH_EITHER,
        ((WithIdentity) blockEitherTemplate.getListBlockHeadersByHashFunction()).getIdentity());
  }

  @Test
  public void testListBlockHeadersByHeight() {
    final BlockBaseTemplate base = mock(BlockBaseTemplate.class);
    final ResultOrErrorFuture<List<BlockHeader>> future =
        ResultOrErrorFutureFactory.supply(() -> new ArrayList<BlockHeader>());
    when(base.getListBlockHeadersByHeightFunction()).thenReturn((h, c) -> future);

    final BlockEitherTemplate blockEitherTemplate = supplyBlockEitherTemplate(base);

    final ResultOrError<List<BlockHeader>> blockHeaders =
        blockEitherTemplate.listBlockHeaders(randomUUID().hashCode(), randomUUID().hashCode());
    assertTrue(blockHeaders.hasResult());
    assertEquals(BLOCK_LIST_HEADERS_BY_HEIGHT_EITHER,
        ((WithIdentity) blockEitherTemplate.getListBlockHeadersByHeightFunction()).getIdentity());
  }

}
