/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.api.model.BlockchainStatus;
import hera.api.model.NodeStatus;
import hera.api.model.PeerAddress;
import java.util.List;

public interface BlockChainOperation {

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
  List<PeerAddress> listPeers();

  /**
   * Get status of current node.
   *
   * @return node status
   */
  NodeStatus getNodeStatus();

}
