/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.BlockchainStatus;
import hera.api.model.NodeStatus;
import hera.api.model.PeerAddress;
import hera.api.tupleorerror.ResultOrError;
import java.util.List;

@ApiAudience.Public
@ApiStability.Unstable
public interface BlockChainEitherOperation {

  /**
   * Get blockchain status.
   *
   * @return blockchain status or error
   */
  ResultOrError<BlockchainStatus> getBlockchainStatus();

  /**
   * Get blockchain peer addresses.
   *
   * @return peer addresses or error
   */
  ResultOrError<List<PeerAddress>> listPeers();

  /**
   * Get status of current node.
   *
   * @return node status or error
   */
  ResultOrError<NodeStatus> getNodeStatus();

}
