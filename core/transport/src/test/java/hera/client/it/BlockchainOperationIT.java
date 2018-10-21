/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.it;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import hera.api.model.BlockchainStatus;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.strategy.NettyConnectStrategy;
import java.util.List;
import org.junit.Test;

public class BlockchainOperationIT extends AbstractIT {

  @Test
  public void testBlockchainStatusLookup() {
    // make aergo client object
    final AergoClient aergoClient = new AergoClientBuilder()
        .addStrategy(new NettyConnectStrategy(hostname))
        .build();

    // lookup current blockchain status
    final BlockchainStatus status = aergoClient.getBlockchainOperation().getBlockchainStatus();
    logger.info("Current best block hash: {}", status.getBestBlockHash());
    logger.info("Current best block height: {}", status.getBestHeight());

    assertNotNull(status.getBestBlockHash());
    assertTrue(0 < status.getBestHeight());

    // close the client
    aergoClient.close();
  }

  @Test
  public void testNodeStatusLookup() {
    // make aergo client object
    final AergoClient aergoClient = new AergoClientBuilder()
        .addStrategy(new NettyConnectStrategy(hostname))
        .build();

    // lookup status of current connected node
    final NodeStatus nodeStatus = aergoClient.getBlockchainOperation().getNodeStatus();
    logger.info("Current node status: {}", nodeStatus);

    assertNotNull(nodeStatus);

    // close the client
    aergoClient.close();
  }

  @Test
  public void testPeers() {
    final AergoClient aergoClient = new AergoClientBuilder()
        .addStrategy(new NettyConnectStrategy(hostname))
        .build();

    // lookup peers of current connected node
    final List<Peer> peers = aergoClient.getBlockchainOperation().listPeers();
    logger.info("Current node peers: {}", peers);

    // close the client
    aergoClient.close();
  }

}
