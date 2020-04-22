/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.Context;
import hera.ContextStorage;
import hera.EmptyContext;
import hera.Invocation;
import hera.Requester;
import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockMetadata;
import hera.api.model.BytesValue;
import hera.api.model.StreamObserver;
import hera.api.model.Subscription;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

public class BlockTemplateTest extends AbstractTestCase {

  protected final ContextStorage<Context> contextStorage = new UnmodifiableContextStorage(
      EmptyContext.getInstance());

  @Test
  public void testGetBlockMetadataByHash() throws Exception {
    // given
    final BlockTemplate blockTemplate = new BlockTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final BlockMetadata expected = BlockMetadata.newBuilder().build();
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any()))
        .thenReturn(expected);
    blockTemplate.requester = mockRequester;

    // then
    final BlockMetadata actual = blockTemplate.getBlockMetadata(BlockHash.of(BytesValue.EMPTY));
    assertEquals(expected, actual);
  }

  @Test
  public void testGetBlockMetadataByHeight() throws Exception {
    // given
    final BlockTemplate blockTemplate = new BlockTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final BlockMetadata expected = BlockMetadata.newBuilder().build();
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any()))
        .thenReturn(expected);
    blockTemplate.requester = mockRequester;

    // then
    final BlockMetadata actual = blockTemplate.getBlockMetadata(30L);
    assertEquals(expected, actual);
  }

  @Test
  public void testListBlockMetadatasByHash() throws Exception {
    // given
    final BlockTemplate blockTemplate = new BlockTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final List<BlockMetadata> expected = emptyList();
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any()))
        .thenReturn(expected);
    blockTemplate.requester = mockRequester;

    // then
    final List<BlockMetadata> actual = blockTemplate
        .listBlockMetadatas(BlockHash.of(BytesValue.EMPTY), 10);
    assertEquals(expected, actual);
  }

  @Test
  public void testListBlockMetadatasByHeight() throws Exception {
    // given
    final BlockTemplate blockTemplate = new BlockTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final List<BlockMetadata> expected = emptyList();
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any()))
        .thenReturn(expected);
    blockTemplate.requester = mockRequester;

    // then
    final List<BlockMetadata> actual = blockTemplate.listBlockMetadatas(30L, 10);
    assertEquals(expected, actual);
  }

  @Test
  public void testGetBlockByHash() throws Exception {
    // given
    final BlockTemplate blockTemplate = new BlockTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final Block expected = Block.newBuilder().build();
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any()))
        .thenReturn(expected);
    blockTemplate.requester = mockRequester;

    // then
    final Block actual = blockTemplate.getBlock(BlockHash.of(BytesValue.EMPTY));
    assertEquals(expected, actual);
  }

  @Test
  public void testGetBlockByHeight() throws Exception {
    // given
    final BlockTemplate blockTemplate = new BlockTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final Block expected = Block.newBuilder().build();
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any()))
        .thenReturn(expected);
    blockTemplate.requester = mockRequester;

    // then
    final Block actual = blockTemplate.getBlock(30L);
    assertEquals(expected, actual);
  }

  @Test
  public void testSubscribeBlockMetadata() throws Exception {
    // given
    final BlockTemplate blockTemplate = new BlockTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final Subscription<?> expected = mock(Subscription.class);
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any())).thenReturn(expected);
    blockTemplate.requester = mockRequester;

    // then
    final Subscription<BlockMetadata> actual = blockTemplate
        .subscribeBlockMetadata(new StreamObserver<BlockMetadata>() {
          @Override
          public void onNext(BlockMetadata value) {

          }

          @Override
          public void onError(Throwable t) {

          }

          @Override
          public void onCompleted() {

          }
        });
    assertEquals(expected, actual);
  }

  @Test
  public void testSubscribeBlock() throws Exception {
    // given
    final BlockTemplate blockTemplate = new BlockTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final Subscription<?> expected = mock(Subscription.class);
    when(mockRequester.request(ArgumentMatchers.<Invocation<?>>any())).thenReturn(expected);
    blockTemplate.requester = mockRequester;

    // then
    final Subscription<Block> actual = blockTemplate
        .subscribeBlock(new StreamObserver<Block>() {
          @Override
          public void onNext(Block value) {

          }

          @Override
          public void onError(Throwable t) {

          }

          @Override
          public void onCompleted() {

          }
        });
    assertEquals(expected, actual);
  }

}

