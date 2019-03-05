/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.BlockProducer;
import hera.api.model.BlockchainStatus;
import hera.api.model.ChainInfo;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.PeerId;
import hera.api.model.PeerMetric;
import hera.api.model.TxHash;
import hera.api.model.VotingInfo;
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
   * Get chain info of current node.
   *
   * @return a chain info
   */
  ChainInfo getChainInfo();

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
   * Get status of current node.
   *
   * @return node status
   */
  NodeStatus getNodeStatus();

  /**
   * Vote to {@code peerId}.
   *
   * @param account an account to sign to voting transaction.
   * @param peerId a peer id to vote
   * @param nonce an nonce which is used in a transaction
   * @return voting transaction hash
   */
  TxHash vote(Account account, PeerId peerId, long nonce);

  /**
   * Get elected block producer from the top voted. Note that this is just a voting result. Not
   * really working block block producers since block producers are renewed after one round.
   *
   * @param showCount show count
   * @return elected block producer list
   */
  List<BlockProducer> listElectedBlockProducers(long showCount);

  /**
   * Get votes which {@code accountAddress} votes for.
   *
   * @param accountAddress an account address
   * @return votes list
   */
  List<VotingInfo> listVotesOf(AccountAddress accountAddress);

}
