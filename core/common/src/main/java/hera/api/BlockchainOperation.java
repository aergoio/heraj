/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.BlockchainStatus;
import hera.api.model.ChainIdHash;
import hera.api.model.ChainInfo;
import hera.api.model.ChainStats;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.PeerMetric;
import hera.api.model.ServerInfo;
import java.util.List;

/**
 * Provide blockchain related operations. It provides followings:
 *
 * <ul>
 * <li>lookup chain id hash</li>
 * <li>lookup blockchain status</li>
 * <li>lookup chain info, status</li>
 * <li>lookup server info</li>
 * <li>lookup peer info</li>
 * <li>lookup node info</li>
 * </ul>
 *
 * @author Taeik Lim
 *
 */
@ApiAudience.Public
@ApiStability.Unstable
public interface BlockchainOperation {

  /**
   * Get chain id hash.
   *
   * @return a chain id hash
   */
  ChainIdHash getChainIdHash();

  /**
   * Get blockchain status.
   *
   * @return blockchain status
   */
  BlockchainStatus getBlockchainStatus();

  /**
   * Get chain info of current node.
   *
   * @return a chain info
   */
  ChainInfo getChainInfo();

  /**
   * Get chain stats of current node.
   *
   * @return a chain stats
   */
  ChainStats getChainStats();

  /**
   * Get blockchain peer addresses filtering hidden peers and itself.
   *
   * @return peer addresses
   */
  List<Peer> listPeers();

  /**
   * Get blockchain peer addresses.
   *
   * @param showHidden whether to show hidden peers
   * @param showSelf whether to show node which receives request itself
   *
   * @return peer addresses
   */
  List<Peer> listPeers(boolean showHidden, boolean showSelf);

  /**
   * Get metrics of peers.
   *
   * @return peer metrics
   */
  List<PeerMetric> listPeerMetrics();

  /**
   * Get server info.
   *
   * @param categories a categories
   *
   * @return server info
   */
  ServerInfo getServerInfo(List<String> categories);

  /**
   * Get status of current node.
   *
   * @return node status
   */
  NodeStatus getNodeStatus();

}
