/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import hera.api.model.BlockchainStatus;
import hera.api.model.NodeStatus;
import hera.api.model.PeerAddress;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class BlockChainTemplateIT extends AbstractIT {

  protected BlockChainEitherTemplate blockChainTemplate = null;

  @Before
  public void setUp() {
    super.setUp();
    blockChainTemplate = new BlockChainEitherTemplate(channel);
  }

  @Test
  public void testGetBlockchainStatus() {
    final BlockchainStatus status = blockChainTemplate.getBlockchainStatus().getResult();
    assertNotNull(status);
    assertTrue(0 < status.getBestHeight());
    assertTrue(!status.getBestBlockHash().getBytesValue().isEmpty());
  }

  @Test
  public void testGetNodeStatus() {
    final NodeStatus nodeStatus = blockChainTemplate.getNodeStatus().getResult();
    assertNotNull(nodeStatus);
    assertTrue(0 < nodeStatus.getModuleStatus().size());
  }

  @Test
  public void testListPeers() {
    final List<PeerAddress> peers = blockChainTemplate.listPeers().getResult();
    assertNotNull(peers);
  }

}
