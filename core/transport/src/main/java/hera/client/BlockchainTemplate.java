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
import java.util.concurrent.Callable;

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
    return request(new Callable<BlockchainStatus>() {
      @Override
      public BlockchainStatus call() throws Exception {
        return requester.request(blockchainMethods
            .getBlockchainStatus()
            .toInvocation());
      }
    });
  }

  @Override
  public ChainInfo getChainInfo() {
    return request(new Callable<ChainInfo>() {
      @Override
      public ChainInfo call() throws Exception {
        return requester.request(blockchainMethods
            .getChainInfo()
            .toInvocation());
      }
    });
  }

  @Override
  public ChainStats getChainStats() {
    return request(new Callable<ChainStats>() {
      @Override
      public ChainStats call() throws Exception {
        return requester.request(blockchainMethods
            .getChainStats()
            .toInvocation());
      }
    });
  }

  @Override
  public List<Peer> listPeers() {
    return listPeers(false, false);
  }

  @Override
  public List<Peer> listPeers(final boolean showHidden, final boolean showSelf) {
    return request(new Callable<List<Peer>>() {
      @Override
      public List<Peer> call() throws Exception {
        return requester.request(blockchainMethods
            .getListPeers()
            .toInvocation(Arrays.<Object>asList(showHidden, showSelf)));
      }
    });
  }

  @Override
  public List<PeerMetric> listPeerMetrics() {
    return request(new Callable<List<PeerMetric>>() {
      @Override
      public List<PeerMetric> call() throws Exception {
        return requester.request(blockchainMethods
            .getListPeersMetrics()
            .toInvocation());
      }
    });
  }

  @Override
  public ServerInfo getServerInfo(final List<String> categories) {
    return request(new Callable<ServerInfo>() {
      @Override
      public ServerInfo call() throws Exception {
        return requester.request(blockchainMethods
            .getServerInfo()
            .toInvocation(Arrays.<Object>asList(categories)));
      }
    });
  }

  @Override
  public NodeStatus getNodeStatus() {
    return request(new Callable<NodeStatus>() {
      @Override
      public NodeStatus call() throws Exception {
        return requester.request(blockchainMethods
            .getNodeStatus()
            .toInvocation());
      }
    });
  }
}
