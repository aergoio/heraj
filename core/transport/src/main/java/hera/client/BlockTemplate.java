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

class BlockTemplate extends AbstractTemplate implements BlockOperation {

  protected final BlockMethods blockMethods = new BlockMethods();

  BlockTemplate(final ContextStorage<Context> contextStorage) {
    super(contextStorage);
  }

  @Override
  public BlockMetadata getBlockMetadata(final BlockHash blockHash) {
    return request(blockMethods.getBlockMetadataByHash(), Arrays.<Object>asList(blockHash));
  }

  @Override
  public BlockMetadata getBlockMetadata(final long height) {
    return request(blockMethods.getBlockMetadataByHeight(), Arrays.<Object>asList(height));
  }

  @Override
  public List<BlockMetadata> listBlockMetadatas(final BlockHash blockHash, final int size) {
    return request(blockMethods.getListBlockMetadatasByHash(),
        Arrays.<Object>asList(blockHash, size));
  }

  @Override
  public List<BlockMetadata> listBlockMetadatas(final long height, final int size) {
    return request(blockMethods.getListBlockMetadatasByHeight(),
        Arrays.<Object>asList(height, size));
  }

  @Override
  public Block getBlock(final BlockHash blockHash) {
    return request(blockMethods.getBlockByHash(), Arrays.<Object>asList(blockHash));
  }

  @Override
  public Block getBlock(final long height) {
    return request(blockMethods.getBlockByHeight(), Arrays.<Object>asList(height));
  }

  @Override
  public Subscription<BlockMetadata> subscribeNewBlockMetadata(
      final StreamObserver<BlockMetadata> observer) {
    return subscribeBlockMetadata(observer);
  }

  @Override
  public Subscription<Block> subscribeNewBlock(final StreamObserver<Block> observer) {
    return subscribeBlock(observer);
  }

  @Override
  public Subscription<BlockMetadata> subscribeBlockMetadata(
      final StreamObserver<BlockMetadata> observer) {
    return request(blockMethods.getSubscribeBlockMetadata(), Arrays.<Object>asList(observer));
  }

  @Override
  public Subscription<Block> subscribeBlock(final StreamObserver<Block> observer) {
    return request(blockMethods.getSubscribeBlock(), Arrays.<Object>asList(observer));
  }

}
