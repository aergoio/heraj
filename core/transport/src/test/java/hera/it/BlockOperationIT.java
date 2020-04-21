/*
 * @copyright defined in LICENSE.txt
 */

package hera.it;

import static hera.api.model.BytesValue.of;
import static java.lang.System.currentTimeMillis;
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
import hera.api.transaction.NonceProvider;
import hera.api.transaction.SimpleNonceProvider;
import hera.client.AergoClient;
import hera.key.AergoKey;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class BlockOperationIT extends AbstractIT {

  protected static AergoClient aergoClient;
  protected final NonceProvider nonceProvider = new SimpleNonceProvider();
  protected final AergoKey rich = AergoKey
      .of("47aK1s1QDCrEvuDRxjx3xznB5naz6juRnVGFjH4hyZRkYDxN6yTHfBQyNQcEEzv42SjQdLx8D", "1234");

  @BeforeClass
  public static void before() {
    final TestClientFactory clientFactory = new TestClientFactory();
    aergoClient = clientFactory.get();
  }

  @AfterClass
  public static void after() throws Exception {
    aergoClient.close();
  }

  @Test
  public void shouldFetchBlockMetadataByHash() {
    // when
    final BlockchainStatus status = aergoClient.getBlockchainOperation().getBlockchainStatus();
    final BlockHash hash = status.getBestBlockHash();

    final BlockMetadata metadata = aergoClient.getBlockOperation().getBlockMetadata(hash);

    // when
    assertEquals(hash, metadata.getBlockHash());
  }

  @Test
  public void shouldFetchBlockMetadataFailOnInvalidHash() {
    try {
      // when
      final BlockHash hash = new BlockHash("8WTYmYgmEGH9UYRYPzGTowS5vhPLumGyb3Pq9UQ3zcRv");
      aergoClient.getBlockOperation().getBlockMetadata(hash);
      fail();
    } catch (Exception e) {
      // then
    }
  }

  @Test
  public void shouldFetchBlockMetadataByHeight() {
    // when
    final BlockchainStatus status = aergoClient.getBlockchainOperation().getBlockchainStatus();
    final BlockHash hash = status.getBestBlockHash();
    final long height = status.getBestHeight();
    final BlockMetadata metadata = aergoClient.getBlockOperation().getBlockMetadata(height);

    // when
    assertEquals(hash, metadata.getBlockHash());
  }

  @Test
  public void shouldFetchBlockMetadataFailOnInvalidHeight() {
    try {
      // when
      final long height = currentTimeMillis() % 2 == 0 ? Long.MAX_VALUE : -1;
      aergoClient.getBlockOperation().getBlockMetadata(height);
      fail();
    } catch (Exception e) {
      // then
    }
  }

  @Test
  public void shouldFetchBlockByHash() {
    // when
    final BlockchainStatus status = aergoClient.getBlockchainOperation().getBlockchainStatus();
    final BlockHash hash = status.getBestBlockHash();

    final Block block = aergoClient.getBlockOperation().getBlock(hash);

    // when
    assertEquals(hash, block.getHash());
  }

  @Test
  public void shouldFetchBlockFailOnInvalidHash() {
    try {
      // when
      final BlockHash hash = new BlockHash("8WTYmYgmEGH9UYRYPzGTowS5vhPLumGyb3Pq9UQ3zcRv");
      aergoClient.getBlockOperation().getBlock(hash);
      fail();
    } catch (Exception e) {
      // then
    }
  }

  @Test
  public void shouldFetchBlockByHeight() {
    // when
    final BlockchainStatus status = aergoClient.getBlockchainOperation().getBlockchainStatus();
    final BlockHash hash = status.getBestBlockHash();
    final long height = status.getBestHeight();
    final Block block = aergoClient.getBlockOperation().getBlock(height);

    // when
    assertEquals(hash, block.getHash());
  }

  @Test
  public void shouldFetchBlockFailOnInvalidHeight() {
    try {
      // when
      final long height = currentTimeMillis() % 2 == 0 ? Long.MAX_VALUE : -1;
      aergoClient.getBlockOperation().getBlock(height);
      fail();
    } catch (Exception e) {
      // then
    }
  }

  @Test
  public void shouldListBlockMetadataByHash() {
    // when
    final BlockchainStatus status = aergoClient.getBlockchainOperation().getBlockchainStatus();
    final BlockHash hash = status.getBestBlockHash();
    final int size = 10;
    final List<BlockMetadata> metadata =
        aergoClient.getBlockOperation().listBlockMetadatas(hash, size);

    // when
    assertEquals(size, metadata.size());
  }

  @Test
  public void shouldListBlockMetadataFailOnInvalidHash() {
    try {
      // when
      final BlockHash hash = new BlockHash("8WTYmYgmEGH9UYRYPzGTowS5vhPLumGyb3Pq9UQ3zcRv");
      aergoClient.getBlockOperation().listBlockMetadatas(hash, 10);
      fail();
    } catch (Exception e) {
      // then
    }
  }

  @Test
  public void shouldListBlockMetadataFailOnValidHashAndInvalidSize() {
    try {
      // when
      final BlockchainStatus status = aergoClient.getBlockchainOperation().getBlockchainStatus();
      final BlockHash hash = status.getBestBlockHash();
      aergoClient.getBlockOperation().listBlockMetadatas(hash, -1);
      fail();
    } catch (Exception e) {
      // then
    }
  }

  @Test
  public void shouldListBlockMetadataByHeight() {
    // when
    final BlockchainStatus status = aergoClient.getBlockchainOperation().getBlockchainStatus();
    final long height = status.getBestHeight();
    final int size = 10;
    final List<BlockMetadata> metadata =
        aergoClient.getBlockOperation().listBlockMetadatas(height, size);

    // when
    assertEquals(size, metadata.size());
  }

  @Test
  public void shouldListBlockMetadataFailOnInvalidHeight() {
    try {
      // when
      final long height = currentTimeMillis() % 2 == 0 ? Long.MAX_VALUE : -1;
      aergoClient.getBlockOperation().listBlockMetadatas(height, 10);
      fail();
    } catch (Exception e) {
      // then
    }
  }

  @Test
  public void shouldListBlockMetadataFailOnValidHeightAndInvalidSize() {
    try {
      // when
      final BlockchainStatus status = aergoClient.getBlockchainOperation().getBlockchainStatus();
      final long height = status.getBestHeight();
      aergoClient.getBlockOperation().listBlockMetadatas(height, -1);
      fail();
    } catch (Exception e) {
      // then
    }
  }

  @Test
  public void testInvalidBlockMetadatasLookup() {
    final BlockchainStatus status = aergoClient.getBlockchainOperation().getBlockchainStatus();

    try {
      // when
      aergoClient.getBlockOperation()
          .listBlockMetadatas(new BlockHash(of(randomUUID().toString().getBytes())), 1);
      // fail(); // TODO : uncomment after fixed in a server
    } catch (Exception e) {
      // then
    }

    try {
      // and when
      aergoClient.getBlockOperation().listBlockMetadatas(status.getBestBlockHash(), -1);
      fail();
    } catch (Exception e) {
      // then
    }

    try {
      // and when
      aergoClient.getBlockOperation().listBlockMetadatas(Long.MAX_VALUE, 1);
      // fail(); // TODO : uncomment after fixed in a server
    } catch (Exception e) {
      // then
    }

    try {
      // and when
      aergoClient.getBlockOperation().listBlockMetadatas(-1, 1);
      fail();
    } catch (Exception e) {
      // then
    }

    try {
      // and when
      aergoClient.getBlockOperation().listBlockMetadatas(status.getBestHeight(), -1);
      fail();
    } catch (Exception e) {
      // then
    }
  }

  @Test
  public void shouldSubcribeBlockMetadata() throws InterruptedException {
    // when
    final int count = 3;
    final CountDownLatch latch = new CountDownLatch(count);
    final Subscription<BlockMetadata> subscription = aergoClient.getBlockOperation()
        .subscribeBlockMetadata(new StreamObserver<BlockMetadata>() {

          @Override
          public void onNext(BlockMetadata value) {
            logger.debug("Next block metadata : {}", value);
            latch.countDown();
          }

          @Override
          public void onError(Throwable t) {
          }

          @Override
          public void onCompleted() {
          }
        });
    for (int i = 0; i < count; ++i) {
      waitForNextBlockToGenerate();
    }
    subscription.unsubscribe();

    // then
    assertTrue(subscription.isUnsubscribed());
    assertEquals(0, latch.getCount());
  }

  @Test
  public void shouldSubcribeBlock() throws InterruptedException {
    // when
    final int count = 3;
    final CountDownLatch latch = new CountDownLatch(count);
    final Subscription<Block> subscription = aergoClient.getBlockOperation()
        .subscribeBlock(new StreamObserver<Block>() {

          @Override
          public void onNext(Block value) {
            logger.debug("Next block : {}", value);
            latch.countDown();
          }

          @Override
          public void onError(Throwable t) {
          }

          @Override
          public void onCompleted() {
          }
        });
    for (int i = 0; i < count; ++i) {
      waitForNextBlockToGenerate();
    }
    subscription.unsubscribe();

    // then
    assertTrue(subscription.isUnsubscribed());
    assertEquals(0, latch.getCount());
  }

}
