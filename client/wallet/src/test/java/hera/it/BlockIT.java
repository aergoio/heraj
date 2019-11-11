/*
 * @copyright defined in LICENSE.txt
 */

package hera.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import hera.api.model.Block;
import hera.api.model.BlockMetadata;
import hera.api.model.BlockchainStatus;
import hera.api.model.StreamObserver;
import hera.api.model.Subscription;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

public class BlockIT extends AbstractWalletApiIT {

  @Test
  public void shouldGetBlockInfoByBestInfo() {
    // when
    final BlockchainStatus blockchainStatus = walletApi.queryApi().getBlockchainStatus();

    // then
    final BlockMetadata metaByHash =
        walletApi.queryApi().getBlockMetadata(blockchainStatus.getBestBlockHash());
    final BlockMetadata metaByHeight =
        walletApi.queryApi().getBlockMetadata(blockchainStatus.getBestHeight());
    assertEquals(metaByHash, metaByHeight);

    // and then
    final Block blockByHash = walletApi.queryApi().getBlock(blockchainStatus.getBestBlockHash());
    final Block blockByHeight = walletApi.queryApi().getBlock(blockchainStatus.getBestHeight());
    assertEquals(blockByHash, blockByHeight);
  }

  @Test
  public void shouldListBlocksByBestInfo() {
    // when
    final BlockchainStatus blockchainStatus = walletApi.queryApi().getBlockchainStatus();

    // then
    final List<BlockMetadata> metasByHash =
        walletApi.queryApi().listBlockMetadatas(blockchainStatus.getBestBlockHash(), 10);
    final List<BlockMetadata> metasByHeight =
        walletApi.queryApi().listBlockMetadatas(blockchainStatus.getBestHeight(), 10);
    assertEquals(metasByHash, metasByHeight);
  }

  @Test
  public void shouldCountdownOnNewBlockMetadata() throws InterruptedException {
    // when
    final CountDownLatch latch = new CountDownLatch(3);
    final StreamObserver<BlockMetadata> callback = new StreamObserver<BlockMetadata>() {

      @Override
      public void onNext(BlockMetadata value) {
        logger.debug("New block metadata: {}", value);
        latch.countDown();
      }

      @Override
      public void onError(Throwable t) {}

      @Override
      public void onCompleted() {}
    };

    // then
    final Subscription<BlockMetadata> subscription =
        walletApi.queryApi().subscribeNewBlockMetadata(callback);
    latch.await();
    assertEquals(0L, latch.getCount());

    // and then
    subscription.unsubscribe();
    assertTrue(subscription.isUnsubscribed());
  }

  @Test
  public void shouldStoreNewBlockWithTimeout() throws InterruptedException {
    // when
    final Object lock = new Object();
    final Map<Long, Block> blockNumber2Block = new HashMap<>();
    final AtomicInteger atomicInteger = new AtomicInteger();
    final StreamObserver<Block> callback = new StreamObserver<Block>() {

      @Override
      public void onNext(Block value) {
        logger.debug("New block: {}", value);
        synchronized (lock) {
          atomicInteger.incrementAndGet();
          blockNumber2Block.put(value.getBlockNumber(), value);
        }
      }

      @Override
      public void onError(Throwable t) {}

      @Override
      public void onCompleted() {}
    };

    // then
    final Subscription<Block> subscription = walletApi.queryApi().subscribeNewBlock(callback);
    Thread.sleep(5000L);
    synchronized (lock) {
      assertEquals(atomicInteger.get(), blockNumber2Block.size());
    }

    // and then
    subscription.unsubscribe();
    assertTrue(subscription.isUnsubscribed());
  }

}
