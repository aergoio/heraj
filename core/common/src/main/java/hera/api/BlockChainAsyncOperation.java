/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.api.model.BlockchainStatus;
import hera.api.model.NodeStatus;
import hera.api.model.PeerAddress;
import hera.api.tupleorerror.ResultOrErrorFuture;
import java.util.List;

public interface BlockChainAsyncOperation {

  /**
   * Get blockchain status asynchronously.
   *
   * @return future of blockchain status or error
   */
  ResultOrErrorFuture<BlockchainStatus> getBlockchainStatus();

  /**
   * Get blockchain peer addresses asynchronously.
   *
   * @return future of peer addresses or error
   */
  ResultOrErrorFuture<List<PeerAddress>> listPeers();

  /**
   * Get status of current node asynchronously.
   *
   * @return future of node status or error
   */
  ResultOrErrorFuture<NodeStatus> getNodeStatus();

}
