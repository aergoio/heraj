/*
 * @copyright defined in LICENSE.txt
 */

package hera.api;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountTotalVote;
import hera.api.model.BlockchainStatus;
import hera.api.model.ChainIdHash;
import hera.api.model.ChainInfo;
import hera.api.model.ElectedCandidate;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.PeerMetric;
import hera.api.model.ServerInfo;
import hera.api.model.TxHash;
import java.util.List;

/**
 * Provide blockchain related operations. It provides followings:
 *
 * <ul>
 *  <li>lookup chain id hash</li>
 *  <li>lookup blockchain status, node status, peers, server info</li>
 *  <li>voting related operations</li>
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

  /**
   * Vote to {@code candidates}.
   *
   * @param account an account to sign to voting transaction.
   * @param voteId a vote id (pre-defined : "voteBP")
   * @param candidates a candidates
   * @param nonce an nonce which is used in a transaction
   *
   * @return voting transaction hash
   */
  TxHash vote(Account account, String voteId, List<String> candidates, long nonce);

  /**
   * Get elected candidates per {@code voteId} for current round.
   *
   * @param voteId a vote id
   * @param showCount a show count
   *
   * @return a vote total
   */
  List<ElectedCandidate> listElected(String voteId, int showCount);

  /**
   * Get votes which {@code accountAddress} votes for.
   *
   * @param accountAddress an account address
   *
   * @return voting info
   */
  AccountTotalVote getVotesOf(AccountAddress accountAddress);

}
