/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.Context;
import hera.ContextStorage;
import hera.api.BlockchainOperation;
import hera.api.model.BlockchainStatus;
import hera.api.model.ChainIdHash;
import hera.api.model.ChainInfo;
import hera.api.model.ChainStats;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.PeerMetric;
import hera.api.model.ServerInfo;
import java.util.Arrays;
import java.util.List;

class BlockchainTemplate extends AbstractTemplate implements BlockchainOperation {

  protected final BlockchainMethods blockchainMethods = new BlockchainMethods();

  BlockchainTemplate(final ContextStorage<Context> contextStorage) {
    super(contextStorage);
  }

  @Override
  public ChainIdHash getChainIdHash() {
    return getBlockchainStatus().getChainIdHash();
  }

  @Override
  public BlockchainStatus getBlockchainStatus() {
    return request(blockchainMethods.getBlockchainStatus());
  }

  @Override
  public ChainInfo getChainInfo() {
    return request(blockchainMethods.getChainInfo());
  }

  @Override
  public ChainStats getChainStats() {
    return request(blockchainMethods.getChainStats());
  }

  @Override
  public List<Peer> listPeers() {
    return listPeers(false, false);
  }

  @Override
  public List<Peer> listPeers(final boolean showHidden, final boolean showSelf) {
    return request(blockchainMethods.getListPeers(), Arrays.<Object>asList(showHidden, showSelf));
  }

  @Override
  public List<PeerMetric> listPeerMetrics() {
    return request(blockchainMethods.getListPeersMetrics());
  }

  @Override
  public ServerInfo getServerInfo(final List<String> categories) {
    return request(blockchainMethods.getServerInfo(), Arrays.<Object>asList(categories));
  }

  @Override
  public NodeStatus getNodeStatus() {
    return request(blockchainMethods.getNodeStatus());
  }
}
