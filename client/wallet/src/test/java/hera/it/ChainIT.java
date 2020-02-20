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
import hera.api.transaction.NonceProvider;
import hera.api.transaction.SimpleNonceProvider;
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

  protected final NonceProvider nonceProvider = new SimpleNonceProvider();
  protected final AergoKey rich = AergoKey
      .of("483wcRWd1SbvATUXv7rMpoa9wg14bj35sPyzj3noSNf3NDHs5ABLhLFguJLF4jsfxi9KFJamT", "1234");
  protected WalletApi walletApi;

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
    walletApi = new WalletApiFactory().create(keyStore);
    walletApi.bind(aergoClient);
  }

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
