/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import static org.junit.Assert.assertEquals;

import hera.api.model.Block;
import hera.api.model.BlockHeader;
import hera.api.model.BlockchainStatus;
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

}
