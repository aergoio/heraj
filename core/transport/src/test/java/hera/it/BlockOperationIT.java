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
import hera.exception.RpcArgumentException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.junit.Test;

public class BlockOperationIT extends AbstractIT {

  @Test
  public void shouldFetchBlockMetadataByHash() {
    // given
    final BlockchainStatus status = aergoClient.getBlockchainOperation().getBlockchainStatus();
    final BlockHash hash = status.getBestBlockHash();

    // when
    final BlockMetadata metadata = aergoClient.getBlockOperation().getBlockMetadata(hash);

    // when
    assertEquals(hash, metadata.getBlockHash());
  }

  @Test
  public void shouldFetchBlockMetadataFailOnInvalidHash() {
    // given
    final BlockHash hash = new BlockHash("8WTYmYgmEGH9UYRYPzGTowS5vhPLumGyb3Pq9UQ3zcRv");

    try {
      // when
      aergoClient.getBlockOperation().getBlockMetadata(hash);
      fail();
    } catch (Exception e) {
      // then
    }
  }

  @Test
  public void shouldFetchBlockMetadataByHeight() {
    // given
    final BlockchainStatus status = aergoClient.getBlockchainOperation().getBlockchainStatus();
    final BlockHash hash = status.getBestBlockHash();
    final long height = status.getBestHeight();

    // when
    final BlockMetadata metadata = aergoClient.getBlockOperation().getBlockMetadata(height);

    // when
    assertEquals(hash, metadata.getBlockHash());
  }

  @Test
  public void shouldFetchBlockMetadataFailOnInvalidHeight() {
    // given
    final long height = currentTimeMillis() % 2 == 0 ? Long.MAX_VALUE : -1;

    try {
      // when
      aergoClient.getBlockOperation().getBlockMetadata(height);
      fail();
    } catch (Exception e) {
      // then
    }
  }

  @Test
  public void shouldListBlockMetadataByHash() {
    // given
    final BlockchainStatus status = aergoClient.getBlockchainOperation().getBlockchainStatus();
    final BlockHash hash = status.getBestBlockHash();

    // when
    final int size = 10;
    final List<BlockMetadata> metadata =
        aergoClient.getBlockOperation().listBlockMetadatas(hash, size);

    // when
    assertEquals(size, metadata.size());
  }

  @Test
  public void shouldListBlockMetadataFailOnInvalidHash() {
    // given
    final BlockHash hash = new BlockHash("8WTYmYgmEGH9UYRYPzGTowS5vhPLumGyb3Pq9UQ3zcRv");

    try {
      // when
      aergoClient.getBlockOperation().listBlockMetadatas(hash, 10);
      fail();
    } catch (Exception e) {
      // then
    }
  }

  @Test
  public void shouldListBlockMetadataFailOnValidHashAndInvalidSize() {
    // given
    final BlockchainStatus status = aergoClient.getBlockchainOperation().getBlockchainStatus();
    final BlockHash hash = status.getBestBlockHash();

    try {
      // when
      aergoClient.getBlockOperation().listBlockMetadatas(hash, -1);
      fail();
    } catch (Exception e) {
      // then
    }
  }

  @Test
  public void shouldListBlockMetadataByHeight() {
    // given
    final BlockchainStatus status = aergoClient.getBlockchainOperation().getBlockchainStatus();
    final long height = status.getBestHeight();

    // when
    final int size = 10;
    final List<BlockMetadata> metadata =
        aergoClient.getBlockOperation().listBlockMetadatas(height, size);

    // when
    assertEquals(size, metadata.size());
  }

  @Test
  public void shouldListBlockMetadataFailOnInvalidHeight() {
    // given
    final long height = currentTimeMillis() % 2 == 0 ? Long.MAX_VALUE : -1;

    try {
      // when
      aergoClient.getBlockOperation().listBlockMetadatas(height, 10);
      fail();
    } catch (Exception e) {
      // then
    }
  }

  @Test
  public void shouldListBlockMetadataFailOnValidHeightAndInvalidSize() {
    // given
    final BlockchainStatus status = aergoClient.getBlockchainOperation().getBlockchainStatus();
    final long height = status.getBestHeight();

    try {
      // when
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
  public void shouldSubcribeBlockMetadata() throws InterruptedException {
    // given
    final int count = 3;
    final CountDownLatch latch = new CountDownLatch(count);

    // when
    Subscription<BlockMetadata> subscription = aergoClient.getBlockOperation()
        .subscribeNewBlockMetadata(new StreamObserver<BlockMetadata>() {

          @Override
          public void onNext(BlockMetadata value) {
            logger.debug("Next block metadata : {}", value);
            latch.countDown();
          }

          @Override
          public void onError(Throwable t) {}

          @Override
          public void onCompleted() {}
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
    // given
    final int count = 3;
    final CountDownLatch latch = new CountDownLatch(count);

    // when
    final Subscription<Block> subscription = aergoClient.getBlockOperation()
        .subscribeNewBlock(new StreamObserver<Block>() {

          @Override
          public void onNext(Block value) {
            logger.debug("Next block : {}", value);
            latch.countDown();
          }

          @Override
          public void onError(Throwable t) {}

          @Override
          public void onCompleted() {}
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
