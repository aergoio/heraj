/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.BLOCKCHAIN_BLOCKCHAINSTATUS;
import static hera.TransportConstants.BLOCKCHAIN_CHAININFO;
import static hera.TransportConstants.BLOCKCHAIN_CHAINSTATS;
import static hera.TransportConstants.BLOCKCHAIN_LIST_PEERS;
import static hera.TransportConstants.BLOCKCHAIN_NODESTATUS;
import static hera.TransportConstants.BLOCKCHAIN_PEERMETRICS;
import static hera.TransportConstants.BLOCKCHAIN_SERVERINFO;
import static hera.api.function.Functions.identify;

import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.BlockchainOperation;
import hera.api.function.Function0;
import hera.api.function.Function1;
import hera.api.function.Function2;
import hera.api.model.BlockchainStatus;
import hera.api.model.ChainIdHash;
import hera.api.model.ChainInfo;
import hera.api.model.ChainStats;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.PeerMetric;
import hera.api.model.ServerInfo;
import hera.client.internal.BlockchainBaseTemplate;
import hera.client.internal.FinishableFuture;
import hera.strategy.StrategyChain;
import io.grpc.ManagedChannel;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@ApiAudience.Private
@ApiStability.Unstable
@RequiredArgsConstructor
public class BlockchainTemplate
    implements BlockchainOperation, ChannelInjectable, ContextProviderInjectable {

  @Getter
  protected BlockchainBaseTemplate blockchainBaseTemplate = new BlockchainBaseTemplate();

  protected ContextProvider contextProvider;

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final StrategyChain strategyChain = StrategyChain.of(contextProvider.get());

  @Override
  public void setChannel(final ManagedChannel channel) {
    getBlockchainBaseTemplate().setChannel(channel);
  }

  @Override
  public void setContextProvider(final ContextProvider contextProvider) {
    this.contextProvider = contextProvider;
    getBlockchainBaseTemplate().setContextProvider(contextProvider);
  }

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function0<FinishableFuture<BlockchainStatus>> blockchainStatusFunction =
      getStrategyChain().apply(identify(getBlockchainBaseTemplate().getBlockchainStatusFunction(),
          BLOCKCHAIN_BLOCKCHAINSTATUS));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function0<FinishableFuture<ChainInfo>> chainInfoFunction =
      getStrategyChain().apply(identify(getBlockchainBaseTemplate().getChainInfoFunction(),
          BLOCKCHAIN_CHAININFO));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function0<FinishableFuture<ChainStats>> chainStatsFunction =
      getStrategyChain().apply(identify(getBlockchainBaseTemplate().getChainStatsFunction(),
          BLOCKCHAIN_CHAINSTATS));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function2<Boolean, Boolean, FinishableFuture<List<Peer>>> listPeersFunction =
      getStrategyChain().apply(
          identify(getBlockchainBaseTemplate().getListPeersFunction(), BLOCKCHAIN_LIST_PEERS));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function0<FinishableFuture<List<PeerMetric>>> listPeerMetricsFunction =
      getStrategyChain().apply(
          identify(getBlockchainBaseTemplate().getListPeersMetricsFunction(),
              BLOCKCHAIN_PEERMETRICS));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<List<String>, FinishableFuture<ServerInfo>> serverInfoFunction =
      getStrategyChain().apply(
          identify(getBlockchainBaseTemplate().getServerInfoFunction(), BLOCKCHAIN_SERVERINFO));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function0<FinishableFuture<NodeStatus>> nodeStatusFunction =
      getStrategyChain().apply(
          identify(getBlockchainBaseTemplate().getNodeStatusFunction(), BLOCKCHAIN_NODESTATUS));

  @Override
  public ChainIdHash getChainIdHash() {
    return getBlockchainStatus().getChainIdHash();
  }

  @Override
  public BlockchainStatus getBlockchainStatus() {
    return getBlockchainStatusFunction().apply().get();
  }

  @Override
  public ChainInfo getChainInfo() {
    return getChainInfoFunction().apply().get();
  }

  @Override
  public ChainStats getChainStats() {
    return getChainStatsFunction().apply().get();
  }

  @Override
  public List<Peer> listPeers() {
    return listPeers(false, false);
  }

  @Override
  public List<Peer> listPeers(final boolean showHidden, final boolean showSelf) {
    return getListPeersFunction().apply(showHidden, showSelf).get();
  }

  @Override
  public List<PeerMetric> listPeerMetrics() {
    return getListPeerMetricsFunction().apply().get();
  }

  @Override
  public ServerInfo getServerInfo(final List<String> categories) {
    return getServerInfoFunction().apply(categories).get();
  }

  @Override
  public NodeStatus getNodeStatus() {
    return getNodeStatusFunction().apply().get();
  }

}
