/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.BlockchainStatus;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.PeerMetric;
import java.util.List;

@ApiAudience.Public
@ApiStability.Unstable
public interface BlockchainOperation {

  /**
   * Get blockchain status.
   *
   * @return blockchain status
   */
  BlockchainStatus getBlockchainStatus();

  /**
   * Get blockchain peer addresses.
   *
   * @return peer addresses
   */
  List<Peer> listPeers();

  /**
   * Get metrics of peers.
   *
   * @return peer metrics
   */
  List<PeerMetric> listPeerMetrics();

  /**
   * Get status of current node.
   *
   * @return node status
   */
  NodeStatus getNodeStatus();

}
