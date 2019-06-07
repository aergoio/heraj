/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import hera.api.model.Account;
import hera.api.model.AccountTotalVote;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.BlockchainStatus;
import hera.api.model.ChainInfo;
import hera.api.model.ChainStats;
import hera.api.model.ElectedCandidate;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.PeerMetric;
import hera.api.model.ServerInfo;
import hera.api.model.StakeInfo;
import hera.exception.RpcCommitException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class BlockchainOperationIT extends AbstractIT {

  @Test
  public void testBlockchainStatus() {
    final BlockchainStatus status = aergoClient.getBlockchainOperation().getBlockchainStatus();
    logger.info("Current blockchain status: {}", status);

    assertNotNull(status.getBestBlockHash());
    assertTrue(0 < status.getBestHeight());
  }

  @Test
  public void testChainInfo() {
    final ChainInfo chainInfo = aergoClient.getBlockchainOperation().getChainInfo();
    logger.info("Current node chain info: {}", chainInfo);

    assertNotNull(chainInfo);
  }

  @Test
  public void testChainStats() {
    final ChainStats chainStats = aergoClient.getBlockchainOperation().getChainStats();
    logger.info("Current node chain stats: {}", chainStats);

    assertNotNull(chainStats);
  }

  @Test
  public void testServerInfo() {
    final ServerInfo serverInfo =
        aergoClient.getBlockchainOperation().getServerInfo(new ArrayList<String>());
    logger.info("Current server info: {}", serverInfo);

    assertNotNull(serverInfo);
  }

  @Test
  public void testNodeStatus() {
    final NodeStatus nodeStatus = aergoClient.getBlockchainOperation().getNodeStatus();
    logger.info("Current node status: {}", nodeStatus);

    assertNotNull(nodeStatus);
  }

  @Test
  public void testListPeers() {
    final List<Peer> peersWithoutArgs = aergoClient.getBlockchainOperation().listPeers();
    logger.info("Current node peers: {}", peersWithoutArgs);
    assertNotNull(peersWithoutArgs);

    final List<Peer> peersWithArgs = aergoClient.getBlockchainOperation().listPeers(true, true);
    logger.info("Current node peers: {}", peersWithArgs);
    assertNotNull(peersWithArgs);
    assertTrue(0 < peersWithArgs.size());
  }

  @Test
  public void testListPeerMetrics() {
    final List<PeerMetric> peerMetrics = aergoClient.getBlockchainOperation().listPeerMetrics();
    logger.info("Current node peer metrics: {}", peerMetrics);

    assertNotNull(peerMetrics);
  }
}
