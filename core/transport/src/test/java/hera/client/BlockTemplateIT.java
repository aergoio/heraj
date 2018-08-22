/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import hera.api.model.Block;
import hera.api.model.BlockchainStatus;
import hera.api.model.Hash;
import org.junit.Before;
import org.junit.Test;

public class BlockTemplateIT extends AbstractIT {

  protected BlockTemplate blockTemplate = null;

  protected BlockChainTemplate blockChainTemplate = null;

  @Before
  public void setUp() {
    super.setUp();
    blockTemplate = new BlockTemplate(channel);
    blockChainTemplate = new BlockChainTemplate(channel);
  }

  @Test
  public void testGetBlockchainStatus() {
    final BlockchainStatus status = blockChainTemplate.getBlockchainStatus().getResult();
    assertNotNull(status);
    assertTrue(0 < status.getBestHeight());
  }

  @Test
  public void testGetBlock() {
    final BlockchainStatus status = blockChainTemplate.getBlockchainStatus().getResult();
    final Hash bestBlockHash = status.getBestBlockHash();
    final Block block = blockTemplate.getBlock(bestBlockHash).getResult();
    assertNotNull(block.getHash());
    assertNotNull(block.getRootHash());
    assertNotNull(block.getTransactionsRootHash());
  }
}