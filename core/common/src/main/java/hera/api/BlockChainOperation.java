/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.api.model.BlockchainStatus;
import hera.api.model.PeerAddress;
import java.util.List;

public interface BlockChainOperation {

  /**
   * Get blockchain status.
   *
   * @return blockchain status
   */
  BlockchainStatus getStatus();

  /**
   * Get blockchain peer addresses.
   *
   * @return peer addresses
   */
  List<PeerAddress> listPeers();
}
