/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.api.model.BlockchainStatus;
import hera.api.model.NodeStatus;
import hera.api.model.PeerAddress;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface BlockChainAsyncOperation {

  /**
   * Get blockchain status asynchronously.
   *
   * @return blockchain status
   */
  CompletableFuture<BlockchainStatus> getBlockchainStatus();

  /**
   * Get blockchain peer addresses asynchronously.
   *
   * @return peer addresses
   */
  CompletableFuture<List<PeerAddress>> listPeers();

  /**
   * Get status of current node asynchronously.
   *
   * @return node status
   */
  CompletableFuture<NodeStatus> getNodeStatus();

}
