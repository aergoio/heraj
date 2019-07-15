/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import hera.api.model.BlockchainStatus;
import hera.api.model.ChainInfo;
import hera.api.model.ChainStats;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.PeerMetric;
import hera.api.model.ServerInfo;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class BlockchainOperationIT extends AbstractIT {

  @Test
  public void shouldFetchBlockchainStatus() {
    // when
    final BlockchainStatus status = aergoClient.getBlockchainOperation().getBlockchainStatus();

    // then
    logger.info("Chain status: {}", status);
    assertTrue(0 < status.getBestHeight());
    assertNotNull(status.getBestBlockHash());
    assertNotNull(status.getConsensus());
    assertNotNull(status.getChainIdHash());
  }

  @Test
  public void shouldFetchChainInfo() {
    // when
    final ChainInfo chainInfo = aergoClient.getBlockchainOperation().getChainInfo();

    // then
    logger.info("Chain info: {}", chainInfo);
    assertNotNull(chainInfo);
  }

  @Test
  public void shouldFetchChainStats() {
    // when
    final ChainStats chainStats = aergoClient.getBlockchainOperation().getChainStats();

    // then
    final String report = chainStats.getReport();
    logger.info("Report: {}", report);
    assertNotNull(report);
  }

  @Test
  public void shouldFetchServerInfo() {
    // when
    final ServerInfo serverInfo =
        aergoClient.getBlockchainOperation().getServerInfo(new ArrayList<String>());

    // then
    logger.info("Server info: {}", serverInfo);
    assertNotNull(serverInfo);
  }

  @Test
  public void shouldListPeersFilteringItself() {
    // when
    final List<Peer> peers = aergoClient.getBlockchainOperation().listPeers();

    // then
    assertNotNull(peers);
  }

  @Test
  public void shouldListPeersNotFilteringItself() {
    // when
    final List<Peer> peers = aergoClient.getBlockchainOperation().listPeers(true, true);

    // then
    assertNotNull(0 < peers.size());
  }

  @Test
  public void shouldListPeerMetrics() {
    // when
    final List<PeerMetric> peerMetrics = aergoClient.getBlockchainOperation().listPeerMetrics();

    // then
    logger.info("Peer metrics: {}");
    assertNotNull(peerMetrics);
  }

  @Test
  public void shouldFetchNodeStatus() {
    // then
    final NodeStatus nodeStatus = aergoClient.getBlockchainOperation().getNodeStatus();

    // then
    logger.info("Node status: {}", nodeStatus);
    assertNotNull(nodeStatus);
  }

}
