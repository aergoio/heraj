/*
 * @copyright defined in LICENSE.txt
 */

package hera.example.wallet.query;

import hera.api.model.BlockchainStatus;
import hera.api.model.ChainIdHash;
import hera.api.model.ChainInfo;
import hera.api.model.ChainStats;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.PeerMetric;
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.example.AbstractExample;
import hera.keystore.InMemoryKeyStore;
import hera.wallet.WalletApi;
import hera.wallet.WalletFactory;
import java.util.List;

public class QueryingChainInfo extends AbstractExample {

  @Override
  public void run() {
    // make wallet api
    WalletApi walletApi = new WalletFactory().create(new InMemoryKeyStore());

    // make aergo client
    AergoClient aergoClient = new AergoClientBuilder()
        .withEndpoint(hostname)
        .withNonBlockingConnect()
        .build();

    // bind aergo client
    walletApi.bind(aergoClient);

    // querying chain id hash, blockchain status
    ChainIdHash chainIdHash = walletApi.queryApi().getChainIdHash();
    BlockchainStatus blockChainStatus = walletApi.queryApi().getBlockchainStatus();
    System.out.printf("ChainIdHash: %s, BlockchainStatus: %s\n", chainIdHash, blockChainStatus);

    // querying chain id hash, blockchain status
    ChainInfo chainInfo = walletApi.queryApi().getChainInfo();
    ChainStats chainStats = walletApi.queryApi().getChainStats();
    System.out.printf("ChainInfo: %s, ChainStats: %s\n", chainInfo, chainStats);

    // querying peers and their metrics
    List<Peer> peers = walletApi.queryApi().listPeers();
    List<PeerMetric> peerMetrics = walletApi.queryApi().listPeerMetrics();
    System.out.printf("Peers: %s, Metrics: %s\n", peers, peerMetrics);

    // querying node status
    NodeStatus nodeStatus = walletApi.queryApi().getNodeStatus();
    System.out.printf("NodeStatus: %s\n", nodeStatus);

    // close the client
    aergoClient.close();
  }

  public static void main(String[] args) {
    new QueryingChainInfo().run();
  }

}
