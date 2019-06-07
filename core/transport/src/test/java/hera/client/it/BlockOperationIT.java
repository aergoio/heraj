/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockMetadata;
import hera.api.model.BlockchainStatus;
import hera.api.model.StreamObserver;
import hera.api.model.Subscription;
import hera.exception.RpcArgumentException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.junit.Test;

public class BlockOperationIT extends AbstractIT {

  @Test
  public void testBlockMetadataLookup() {
    final BlockchainStatus status = aergoClient.getBlockchainOperation().getBlockchainStatus();
    final Block block = aergoClient.getBlockOperation().getBlock(status.getBestBlockHash());
    logger.info("Best block: {}", block);

    final BlockMetadata blockMetadataByHash =
        aergoClient.getBlockOperation().getBlockMetadata(block.getHash());
    final BlockMetadata blockMetadataByHeight =
        aergoClient.getBlockOperation().getBlockMetadata(block.getBlockHeader().getBlockNumber());
    logger.info("Block header by hash: {}", blockMetadataByHash);
    logger.info("Block header by height: {}", blockMetadataByHeight);

    assertEquals(blockMetadataByHash, blockMetadataByHeight);
  }

  @Test
  public void testInvalidBlockMetadataLookup() {
    try {
      aergoClient.getBlockOperation()
          .getBlockMetadata(new BlockHash(of(randomUUID().toString().getBytes())));
      fail();
    } catch (Exception e) {
      // good we expected this
    }

    try {
      aergoClient.getBlockOperation()
          .getBlockMetadata(new BlockHash("8WTYmYgmEGH9UYRYPzGTowS5vhPLumGyb3Pq9UQ3zcRv"));
      fail();
    } catch (Exception e) {
      // good we expected this
    }

    try {
      aergoClient.getBlockOperation().getBlockMetadata(Long.MAX_VALUE);
      fail();
    } catch (Exception e) {
      // good we expected this
    }

    try {
      aergoClient.getBlockOperation().getBlockMetadata(-1);
      fail();
    } catch (RpcArgumentException e) {
      // good we expected this
    }
  }

  @Test
  public void testBlockMetadatasLookup() {
    final BlockchainStatus status = aergoClient.getBlockchainOperation().getBlockchainStatus();
    final Block block = aergoClient.getBlockOperation().getBlock(status.getBestBlockHash());
    logger.info("Best block: {}", block);

    final List<BlockMetadata> blockMetadatasByHash =
        aergoClient.getBlockOperation().listBlockMetadatas(block.getHash(), 10);
    final List<BlockMetadata> blockMetadatasByHeight =
        aergoClient.getBlockOperation().listBlockMetadatas(block.getBlockHeader().getBlockNumber(),
            10);
    logger.info("Block headers by hash: {}", blockMetadatasByHash);
    logger.info("Block headers by height: {}", blockMetadatasByHeight);

    assertEquals(blockMetadatasByHash, blockMetadatasByHeight);
  }

  @Test
  public void testInvalidBlockMetadatasLookup() {
    final BlockchainStatus status = aergoClient.getBlockchainOperation().getBlockchainStatus();

    try {
      aergoClient.getBlockOperation()
          .listBlockMetadatas(new BlockHash(of(randomUUID().toString().getBytes())), 1);
      // fail(); // TODO : uncomment after fixed in a server
    } catch (Exception e) {
      // good we expected this
    }

    try {
      aergoClient.getBlockOperation().listBlockMetadatas(status.getBestBlockHash(), -1);
      fail();
    } catch (RpcArgumentException e) {
      // good we expected this
    }

    try {
      aergoClient.getBlockOperation().listBlockMetadatas(Long.MAX_VALUE, 1);
      // fail(); // TODO : uncomment after fixed in a server
    } catch (Exception e) {
      // good we expected this
    }

    try {
      aergoClient.getBlockOperation().listBlockMetadatas(-1, 1);
      fail();
    } catch (RpcArgumentException e) {
      // good we expected this
    }

    try {
      aergoClient.getBlockOperation().listBlockMetadatas(status.getBestHeight(), -1);
      fail();
    } catch (RpcArgumentException e) {
      // good we expected this
    }
  }

  @Test
  public void testBlockLoopup() {
    final BlockchainStatus status = aergoClient.getBlockchainOperation().getBlockchainStatus();

    final Block blockByHash = aergoClient.getBlockOperation().getBlock(status.getBestBlockHash());
    final Block blockByHeight = aergoClient.getBlockOperation().getBlock(status.getBestHeight());
    logger.info("Block by hash: {}", blockByHash);
    logger.info("Block by height: {}", blockByHeight);

    assertEquals(blockByHash, blockByHeight);

    // lookup previous block by hash
    final Block previousBlock =
        aergoClient.getBlockOperation().getBlock(blockByHash.getBlockHeader().getPreviousHash());
    logger.info("Previous block: {}", previousBlock);

    assertEquals(previousBlock.getHash(), blockByHash.getBlockHeader().getPreviousHash());
    assertEquals(previousBlock.getBlockHeader().getBlockNumber() + 1,
        blockByHash.getBlockHeader().getBlockNumber());
  }

  @Test
  public void testInvalidBlockLookup() {
    try {
      aergoClient.getBlockOperation()
          .getBlock(new BlockHash(of(randomUUID().toString().getBytes())));
      fail();
    } catch (Exception e) {
      // good we expected this
    }

    try {
      aergoClient.getBlockOperation()
          .getBlock(new BlockHash("8WTYmYgmEGH9UYRYPzGTowS5vhPLumGyb3Pq9UQ3zcRv"));
      fail();
    } catch (Exception e) {
      // good we expected this
    }

    try {
      aergoClient.getBlockOperation().getBlock(Long.MAX_VALUE);
      fail();
    } catch (Exception e) {
      // good we expected this
    }

    try {
      aergoClient.getBlockOperation().getBlock(-1);
      fail();
    } catch (RpcArgumentException e) {
      // good we expected this
    }
  }

  @Test
  public void testBlockMetadataSubscription() throws InterruptedException {

    final CountDownLatch latch = new CountDownLatch(3);
    Subscription<BlockMetadata> subscription = aergoClient.getBlockOperation()
        .subscribeNewBlockMetadata(new StreamObserver<BlockMetadata>() {

          @Override
          public void onNext(BlockMetadata value) {
            logger.info("New block metadata: {}", value);
            latch.countDown();
          }

          @Override
          public void onError(Throwable t) {}

          @Override
          public void onCompleted() {}
        });

    Thread.sleep(5000L);
    subscription.unsubscribe();

    assertTrue(subscription.isUnsubscribed());
    assertEquals(0, latch.getCount());
  }

  @Test
  public void testBlockSubscription() throws InterruptedException {

    final CountDownLatch latch = new CountDownLatch(3);
    Subscription<Block> subscription = aergoClient.getBlockOperation()
        .subscribeNewBlock(new StreamObserver<Block>() {

          @Override
          public void onNext(Block value) {
            logger.info("New block: {}", value);
            latch.countDown();
          }

          @Override
          public void onError(Throwable t) {}

          @Override
          public void onCompleted() {}
        });

    Thread.sleep(5000L);
    subscription.unsubscribe();

    assertTrue(subscription.isUnsubscribed());
    assertEquals(0, latch.getCount());
  }

}
