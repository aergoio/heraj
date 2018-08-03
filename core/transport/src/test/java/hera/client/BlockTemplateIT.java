/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static types.AergoRPCServiceGrpc.newBlockingStub;

import hera.api.model.Hash;
import hera.api.model.Block;
import hera.api.model.BlockchainStatus;
import org.junit.Before;
import org.junit.Test;

public class BlockTemplateIT extends AbstractIT {

  protected BlockTemplate blockTemplate = null;

  protected BlockChainTemplate blockChainTemplate = null;

  @Before
  public void setUp() {
    super.setUp();
    blockTemplate = new BlockTemplate(newBlockingStub(channel));
    blockChainTemplate = new BlockChainTemplate(newBlockingStub(channel));
  }

  @Test
  public void testGetBlockchainStatus() {
    final BlockchainStatus status = blockChainTemplate.getBlockchainStatus();
    assertNotNull(status);
    assertTrue(0 < status.getBestHeight());
  }

  @Test
  public void testGetBlock() {
    final BlockchainStatus status = blockChainTemplate.getBlockchainStatus();
    final Hash bestBlockHash = status.getBestBlockHash();
    final Block block = blockTemplate.getBlock(bestBlockHash);
    assertNotNull(block.getHash());
    assertNotNull(block.getRootHash());
    assertNotNull(block.getTransactionsRootHash());
  }
}