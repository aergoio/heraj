/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockHeader;
import hera.api.model.BlockchainStatus;
import hera.client.AergoClientBuilder;
import hera.client.BlockChainEitherTemplate;
import hera.client.BlockEitherTemplate;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class BlockTemplateIT extends AbstractIT {

  protected BlockEitherTemplate blockTemplate = null;

  protected BlockChainEitherTemplate blockChainTemplate = null;

  @Before
  public void setUp() {
    super.setUp();
    blockTemplate = new BlockEitherTemplate(channel, AergoClientBuilder.getDefaultContext());
    blockChainTemplate = new BlockChainEitherTemplate(channel, AergoClientBuilder.getDefaultContext());
  }

  @Test
  public void testGetBlockByHash() {
    final BlockchainStatus status = blockChainTemplate.getBlockchainStatus().getResult();
    final BlockHash bestBlockHash = status.getBestBlockHash();
    final Block block = blockTemplate.getBlock(bestBlockHash).getResult();
    assertNotNull(block);
    assertTrue(!block.getHash().getBytesValue().isEmpty());
    assertTrue(!block.getPreviousHash().getBytesValue().isEmpty());
  }

  @Test
  public void testGetBlockByHeight() {
    final BlockchainStatus status = blockChainTemplate.getBlockchainStatus().getResult();
    final long bestBlockHeight = status.getBestHeight();
    final Block block = blockTemplate.getBlock(bestBlockHeight).getResult();
    assertNotNull(block);
    assertTrue(!block.getHash().getBytesValue().isEmpty());
    assertTrue(!block.getPreviousHash().getBytesValue().isEmpty());
  }

  @Test
  public void testListBlockHeadersByHash() {
    final BlockchainStatus status = blockChainTemplate.getBlockchainStatus().getResult();
    final BlockHash bestBlockHash = status.getBestBlockHash();
    final int size = 3;
    final List<BlockHeader> blockHeaders =
        blockTemplate.listBlockHeaders(bestBlockHash, size).getResult();
    assertNotNull(blockHeaders);
    assertTrue(blockHeaders.size() == size);
  }

  @Test
  public void testListBlockHeadersByHeight() {
    final BlockchainStatus status = blockChainTemplate.getBlockchainStatus().getResult();
    final long bestBlockHeight = status.getBestHeight();
    final int size = 3;
    final List<BlockHeader> blockHeaders =
        blockTemplate.listBlockHeaders(bestBlockHeight, size).getResult();
    assertNotNull(blockHeaders);
    assertTrue(blockHeaders.size() == size);
  }
}
