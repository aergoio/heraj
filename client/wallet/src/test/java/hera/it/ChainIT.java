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
import hera.client.AergoClient;
import hera.key.AergoKey;
import hera.wallet.WalletApi;
import hera.wallet.WalletApiFactory;
import java.util.ArrayList;
import java.util.List;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ChainIT extends AbstractWalletApiIT {

  protected static AergoClient aergoClient;

  protected final AergoKey rich = AergoKey
      .of("483wcRWd1SbvATUXv7rMpoa9wg14bj35sPyzj3noSNf3NDHs5ABLhLFguJLF4jsfxi9KFJamT", "1234");

  @BeforeClass
  public static void before() {
    final TestClientFactory clientFactory = new TestClientFactory();
    aergoClient = clientFactory.get();
  }

  @AfterClass
  public static void after() throws Exception {
    aergoClient.close();
  }

  @Before
  public void setUp() {
  }

  @Test
  public void testQueryChainInfos() {
    final WalletApi walletApi = new WalletApiFactory().create(keyStore);
    final ChainIdHash chainIdHash = walletApi.with(aergoClient).query().getChainIdHash();
    final BlockchainStatus blockchainStatus = walletApi.with(aergoClient).query()
        .getBlockchainStatus();
    assertNotNull(chainIdHash);
    assertEquals(blockchainStatus.getChainIdHash(), chainIdHash);

    final ChainInfo chainInfo = walletApi.with(aergoClient).query().getChainInfo();
    final ChainStats chainStats = walletApi.with(aergoClient).query().getChainStats();
    assertNotNull(chainInfo);
    assertNotNull(chainStats);

    final List<Peer> peers = walletApi.with(aergoClient).query().listPeers();
    final List<PeerMetric> peerMetrics = walletApi.with(aergoClient).query()
        .listPeerMetrics();
    assertNotNull(peers);
    assertNotNull(peerMetrics);

    final ServerInfo serverInfo = walletApi.with(aergoClient).query()
        .getServerInfo(new ArrayList<String>());
    final NodeStatus nodeStatus = walletApi.with(aergoClient).query().getNodeStatus();
    assertNotNull(serverInfo);
    assertNotNull(nodeStatus);
  }

}
