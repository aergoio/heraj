/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import hera.api.model.BlockchainStatus;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.PeerMetric;
import java.util.List;
import org.junit.Test;

public class BlockchainOperationIT extends AbstractIT {

  @Test
  public void testBlockchainStatusLookup() {
    final BlockchainStatus status = aergoClient.getBlockchainOperation().getBlockchainStatus();
    logger.info("Current best block hash: {}", status.getBestBlockHash());
    logger.info("Current best block height: {}", status.getBestHeight());

    assertNotNull(status.getBestBlockHash());
    assertTrue(0 < status.getBestHeight());
  }

  @Test
  public void testNodeStatusLookup() {
    final NodeStatus nodeStatus = aergoClient.getBlockchainOperation().getNodeStatus();
    logger.info("Current node status: {}", nodeStatus);

    assertNotNull(nodeStatus);
  }

  @Test
  public void testPeers() {
    final List<Peer> peers = aergoClient.getBlockchainOperation().listPeers();
    logger.info("Current node peers: {}", peers);
  }

  @Test
  public void testPeerMetrics() {
    final List<PeerMetric> peerMetrics = aergoClient.getBlockchainOperation().listPeerMetrics();
    logger.info("Current node peer metrics: {}", peerMetrics);
  }

}
