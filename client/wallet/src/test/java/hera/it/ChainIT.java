/*
 * @copyright defined in LICENSE.txt
 */

package hera.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import hera.api.model.BlockchainStatus;
import hera.api.model.ChainIdHash;
import hera.api.model.ChainInfo;
import hera.api.model.ChainStats;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.PeerMetric;
import hera.api.model.ServerInfo;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class ChainIT extends AbstractWalletApiIT {

  @Test
  public void testQueryChainInfos() {
    final ChainIdHash chainIdHash = walletApi.queryApi().getChainIdHash();
    final BlockchainStatus blockchainStatus = walletApi.queryApi().getBlockchainStatus();
    assertNotNull(chainIdHash);
    assertEquals(blockchainStatus.getChainIdHash(), chainIdHash);

    final ChainInfo chainInfo = walletApi.queryApi().getChainInfo();
    final ChainStats chainStats = walletApi.queryApi().getChainStats();
    assertNotNull(chainInfo);
    assertNotNull(chainStats);

    final List<Peer> peers = walletApi.queryApi().listPeers();
    final List<PeerMetric> peerMetrics = walletApi.queryApi().listPeerMetrics();
    assertNotNull(peers);
    assertNotNull(peerMetrics);

    final ServerInfo serverInfo = walletApi.queryApi().getServerInfo(new ArrayList<String>());
    final NodeStatus nodeStatus = walletApi.queryApi().getNodeStatus();
    assertNotNull(serverInfo);
    assertNotNull(nodeStatus);
  }

}
