/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.BLOCKCHAIN_BLOCKCHAINSTATUS;
import static hera.TransportConstants.BLOCKCHAIN_LISTPEERS;
import static hera.TransportConstants.BLOCKCHAIN_NODESTATUS;
import static hera.TransportConstants.BLOCKCHAIN_PEERMETRICS;
import static hera.api.function.Functions.identify;

import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.BlockchainOperation;
import hera.api.function.Function0;
import hera.api.model.BlockchainStatus;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.PeerMetric;
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
  private final Function0<FinishableFuture<List<Peer>>> listPeersFunction =
      getStrategyChain()
          .apply(
              identify(getBlockchainBaseTemplate().getListPeersFunction(), BLOCKCHAIN_LISTPEERS));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function0<FinishableFuture<List<PeerMetric>>> listPeerMetricsFunction =
      getStrategyChain().apply(
          identify(getBlockchainBaseTemplate().getListPeersMetricsFunction(),
              BLOCKCHAIN_PEERMETRICS));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function0<FinishableFuture<NodeStatus>> nodeStatusFunction =
      getStrategyChain().apply(
          identify(getBlockchainBaseTemplate().getNodeStatusFunction(), BLOCKCHAIN_NODESTATUS));

  @Override
  public BlockchainStatus getBlockchainStatus() {
    return getBlockchainStatusFunction().apply().get();
  }

  @Override
  public List<Peer> listPeers() {
    return getListPeersFunction().apply().get();
  }

  @Override
  public List<PeerMetric> listPeerMetrics() {
    return getListPeerMetricsFunction().apply().get();
  }

  @Override
  public NodeStatus getNodeStatus() {
    return getNodeStatusFunction().apply().get();
  }

}
