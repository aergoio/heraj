/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockHeader;
import hera.api.model.BlockchainStatus;
import hera.exception.RpcArgumentException;
import java.util.List;
import org.junit.Test;

public class BlockOperationIT extends AbstractIT {

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
        aergoClient.getBlockOperation().getBlock(blockByHash.getPreviousHash());
    logger.info("Previous block: {}", previousBlock);

    assertEquals(previousBlock.getHash(), blockByHash.getPreviousHash());
    assertEquals(previousBlock.getBlockNumber() + 1, blockByHash.getBlockNumber());
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
          .getBlock(new BlockHash(() -> "8WTYmYgmEGH9UYRYPzGTowS5vhPLumGyb3Pq9UQ3zcRv"));
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
  public void testBlockHeaderLookup() {
    final BlockchainStatus status = aergoClient.getBlockchainOperation().getBlockchainStatus();
    final Block block = aergoClient.getBlockOperation().getBlock(status.getBestBlockHash());
    logger.info("Best block: {}", block);

    final List<BlockHeader> blockHeadersByHash =
        aergoClient.getBlockOperation().listBlockHeaders(block.getHash(), 10);
    final List<BlockHeader> blockHeadersByHeight =
        aergoClient.getBlockOperation().listBlockHeaders(block.getBlockNumber(), 10);
    logger.info("Block headers by hash: {}", blockHeadersByHash);
    logger.info("Block headers by height: {}", blockHeadersByHeight);

    assertEquals(blockHeadersByHash, blockHeadersByHeight);
  }

  @Test
  public void testInvalidBlockHeaderLookup() {
    final BlockchainStatus status = aergoClient.getBlockchainOperation().getBlockchainStatus();

    try {
      aergoClient.getBlockOperation()
          .listBlockHeaders(new BlockHash(of(randomUUID().toString().getBytes())), 1);
      // fail(); // TODO : uncomment after fixed in a server
    } catch (Exception e) {
      // good we expected this
    }

    try {
      aergoClient.getBlockOperation().listBlockHeaders(status.getBestBlockHash(), -1);
      fail();
    } catch (RpcArgumentException e) {
      // good we expected this
    }

    try {
      aergoClient.getBlockOperation().listBlockHeaders(Long.MAX_VALUE, 1);
      // fail(); // TODO : uncomment after fixed in a server
    } catch (Exception e) {
      // good we expected this
    }

    try {
      aergoClient.getBlockOperation().listBlockHeaders(-1, 1);
      fail();
    } catch (RpcArgumentException e) {
      // good we expected this
    }

    try {
      aergoClient.getBlockOperation().listBlockHeaders(status.getBestHeight(), -1);
      fail();
    } catch (RpcArgumentException e) {
      // good we expected this
    }
  }

}
