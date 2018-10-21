/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import static org.junit.Assert.assertEquals;
import hera.api.model.Block;
import hera.api.model.BlockHeader;
import hera.api.model.BlockchainStatus;
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.strategy.NettyConnectStrategy;
import java.util.List;
import org.junit.Test;

public class BlockOperationIT extends AbstractIT {

  @Test
  public void testBlockLoopup() {
    // make aergo client object
    final AergoClient aergoClient = new AergoClientBuilder()
        .addStrategy(new NettyConnectStrategy(hostname))
        .build();

    // lookup current blockchain status
    final BlockchainStatus status = aergoClient.getBlockchainOperation().getBlockchainStatus();

    // lookup block by best block hash
    final Block blockByHash = aergoClient.getBlockOperation().getBlock(status.getBestBlockHash());
    logger.info("Block by hash: {}", blockByHash);

    // lookup block by best height
    final Block blockByHeight = aergoClient.getBlockOperation().getBlock(status.getBestHeight());
    logger.info("Block by height: {}", blockByHeight);

    assertEquals(blockByHash, blockByHeight);

    // lookup previous block by hash
    final Block previousBlock =
        aergoClient.getBlockOperation().getBlock(blockByHash.getPreviousHash());
    logger.info("Previous block: {}", previousBlock);

    assertEquals(previousBlock.getHash(), blockByHash.getPreviousHash());
    assertEquals(previousBlock.getBlockNumber() + 1, blockByHash.getBlockNumber());

    // close the client
    aergoClient.close();
  }

  @Test
  public void testBlockHeaderLookup() {
    // make aergo client object
    final AergoClient aergoClient = new AergoClientBuilder()
        .addStrategy(new NettyConnectStrategy(hostname))
        .build();

    // lookup best block
    final BlockchainStatus status = aergoClient.getBlockchainOperation().getBlockchainStatus();
    final Block block = aergoClient.getBlockOperation().getBlock(status.getBestBlockHash());
    logger.info("Best block: {}", block);

    // lookup 10 block headers starting from best block backward with best block hash
    final List<BlockHeader> blockHeadersByHash =
        aergoClient.getBlockOperation().listBlockHeaders(block.getHash(), 10);
    logger.info("Block headers by hash: {}", blockHeadersByHash);

    // lookup 10 block headers starting from best block backward with best block height
    final List<BlockHeader> blockHeadersByHeight =
        aergoClient.getBlockOperation().listBlockHeaders(block.getBlockNumber(), 10);
    logger.info("Block headers by height: {}", blockHeadersByHeight);

    assertEquals(blockHeadersByHash, blockHeadersByHeight);

    // close the client
    aergoClient.close();
  }

}
