/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.Context;
import hera.ContextStorage;
import hera.api.BlockOperation;
import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockMetadata;
import hera.api.model.StreamObserver;
import hera.api.model.Subscription;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

class BlockTemplate extends AbstractTemplate implements BlockOperation {

  protected final BlockMethods blockMethods = new BlockMethods();

  BlockTemplate(final ContextStorage<Context> contextStorage) {
    super(contextStorage);
  }

  @Override
  public BlockMetadata getBlockMetadata(final BlockHash blockHash) {
    return request(new Callable<BlockMetadata>() {
      @Override
      public BlockMetadata call() throws Exception {
        return requester.request(blockMethods
            .getBlockMetadataByHash()
            .toInvocation(Arrays.<Object>asList(blockHash)));
      }
    });
  }

  @Override
  public BlockMetadata getBlockMetadata(final long height) {
    return request(new Callable<BlockMetadata>() {
      @Override
      public BlockMetadata call() throws Exception {
        return requester.request(blockMethods
            .getBlockMetadataByHeight()
            .toInvocation(Arrays.<Object>asList(height)));
      }
    });
  }

  @Override
  public List<BlockMetadata> listBlockMetadatas(final BlockHash blockHash, final int size) {
    return request(new Callable<List<BlockMetadata>>() {
      @Override
      public List<BlockMetadata> call() throws Exception {
        return requester.request(blockMethods
            .getListBlockMetadatasByHash()
            .toInvocation(Arrays.<Object>asList(blockHash, size)));
      }
    });
  }

  @Override
  public List<BlockMetadata> listBlockMetadatas(final long height, final int size) {
    return request(new Callable<List<BlockMetadata>>() {
      @Override
      public List<BlockMetadata> call() throws Exception {
        return requester.request(blockMethods
            .getListBlockMetadatasByHeight()
            .toInvocation(Arrays.<Object>asList(height, size)));
      }
    });
  }

  @Override
  public Block getBlock(final BlockHash blockHash) {
    return request(new Callable<Block>() {
      @Override
      public Block call() throws Exception {
        return requester.request(blockMethods
            .getBlockByHash()
            .toInvocation(Arrays.<Object>asList(blockHash)));
      }
    });
  }

  @Override
  public Block getBlock(final long height) {
    return request(new Callable<Block>() {
      @Override
      public Block call() throws Exception {
        return requester.request(blockMethods
            .getBlockByHeight()
            .toInvocation(Arrays.<Object>asList(height)));
      }
    });
  }

  @Override
  public Subscription<BlockMetadata> subscribeNewBlockMetadata(
      final StreamObserver<BlockMetadata> observer) {
    return request(new Callable<Subscription<BlockMetadata>>() {
      @Override
      public Subscription<BlockMetadata> call() throws Exception {
        return requester.request(blockMethods
            .getSubscribeBlockMetadata()
            .toInvocation(Arrays.<Object>asList(observer)));
      }
    });
  }

  @Override
  public Subscription<Block> subscribeNewBlock(final StreamObserver<Block> observer) {
    return request(new Callable<Subscription<Block>>() {
      @Override
      public Subscription<Block> call() throws Exception {
        return requester.request(blockMethods
            .getSubscribeBlock()
            .toInvocation(Arrays.<Object>asList(observer)));
      }
    });
  }

}
