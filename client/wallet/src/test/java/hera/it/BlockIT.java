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
import hera.client.AergoClient;
import hera.key.AergoKey;
import hera.wallet.WalletApi;
import hera.wallet.WalletApiFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class BlockIT extends AbstractWalletApiIT {

  protected static AergoClient aergoClient;

  protected final AergoKey rich = AergoKey
      .of("47ZhS5rhhGvgt6CqhMiTEPEjfeKS91dhRDNYesdvDhMYtNPu1YL9dqKu9cWxr8D3W3MPAg62m", "1234");

  @BeforeClass
  public static void before() {
    final TestClientFactory clientFactory = new TestClientFactory();
    aergoClient = clientFactory.get();
  }

  @AfterClass
  public static void after() throws Exception {
    aergoClient.close();
  }

  @Before
  public void setUp() {
  }

  @Test
  public void shouldGetBlockInfoByBestInfo() {
    // when
    final WalletApi walletApi = new WalletApiFactory().create(keyStore);
    final BlockchainStatus blockchainStatus = walletApi.with(aergoClient).query()
        .getBlockchainStatus();

    // then
    final BlockMetadata metaByHash =
        walletApi.with(aergoClient).query()
            .getBlockMetadata(blockchainStatus.getBestBlockHash());
    final BlockMetadata metaByHeight =
        walletApi.with(aergoClient).query()
            .getBlockMetadata(blockchainStatus.getBestHeight());
    assertEquals(metaByHash, metaByHeight);

    // and then
    final Block blockByHash = walletApi.with(aergoClient).query()
        .getBlock(blockchainStatus.getBestBlockHash());
    final Block blockByHeight = walletApi.with(aergoClient).query()
        .getBlock(blockchainStatus.getBestHeight());
    assertEquals(blockByHash, blockByHeight);
  }

  @Test
  public void shouldListBlocksByBestInfo() {
    // when
    final WalletApi walletApi = new WalletApiFactory().create(keyStore);
    final BlockchainStatus blockchainStatus = walletApi.with(aergoClient).query()
        .getBlockchainStatus();

    // then
    final List<BlockMetadata> metasByHash =
        walletApi.with(aergoClient).query()
            .listBlockMetadatas(blockchainStatus.getBestBlockHash(), 10);
    final List<BlockMetadata> metasByHeight =
        walletApi.with(aergoClient).query()
            .listBlockMetadatas(blockchainStatus.getBestHeight(), 10);
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
      public void onError(Throwable t) {
      }

      @Override
      public void onCompleted() {
      }
    };

    // then
    final WalletApi walletApi = new WalletApiFactory().create(keyStore);
    final Subscription<BlockMetadata> subscription =
        walletApi.with(aergoClient).query().subscribeBlockMetadata(callback);
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
      public void onError(Throwable t) {
      }

      @Override
      public void onCompleted() {
      }
    };

    // then
    final WalletApi walletApi = new WalletApiFactory().create(keyStore);
    final Subscription<Block> subscription = walletApi.with(aergoClient).query()
        .subscribeBlock(callback);
    Thread.sleep(5000L);
    synchronized (lock) {
      assertEquals(atomicInteger.get(), blockNumber2Block.size());
    }

    // and then
    subscription.unsubscribe();
    assertTrue(subscription.isUnsubscribed());
  }

}
