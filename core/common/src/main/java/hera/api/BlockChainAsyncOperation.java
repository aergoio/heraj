/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.ContextAware;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.BlockchainStatus;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.tupleorerror.ResultOrErrorFuture;
import java.util.List;

@ApiAudience.Public
@ApiStability.Unstable
public interface BlockChainAsyncOperation extends ContextAware {

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
  ResultOrErrorFuture<List<Peer>> listPeers();

  /**
   * Get status of current node asynchronously.
   *
   * @return future of node status or error
   */
  ResultOrErrorFuture<NodeStatus> getNodeStatus();

}
