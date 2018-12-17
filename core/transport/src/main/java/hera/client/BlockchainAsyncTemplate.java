/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.BLOCKCHAIN_BLOCKCHAINSTATUS_ASYNC;
import static hera.TransportConstants.BLOCKCHAIN_LISTPEERS_ASYNC;
import static hera.TransportConstants.BLOCKCHAIN_NODESTATUS_ASYNC;
import static hera.TransportConstants.BLOCKCHAIN_PEERMETRICS_ASYNC;
import static hera.api.tupleorerror.Functions.identify;

import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.BlockchainAsyncOperation;
import hera.api.model.BlockchainStatus;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.PeerMetric;
import hera.api.tupleorerror.Function0;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.strategy.StrategyChain;
import io.grpc.ManagedChannel;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@ApiAudience.Private
@ApiStability.Unstable
@RequiredArgsConstructor
public class BlockchainAsyncTemplate
    implements BlockchainAsyncOperation, ChannelInjectable, ContextProviderInjectable {

  protected BlockchainBaseTemplate blockchainBaseTemplate = new BlockchainBaseTemplate();

  @Setter
  protected ContextProvider contextProvider;

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final StrategyChain strategyChain = StrategyChain.of(contextProvider.get());

  @Override
  public void setChannel(final ManagedChannel channel) {
    blockchainBaseTemplate.setChannel(channel);
  }

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function0<ResultOrErrorFuture<BlockchainStatus>> blockchainStatusFunction =
      getStrategyChain().apply(identify(blockchainBaseTemplate.getBlockchainStatusFunction(),
          BLOCKCHAIN_BLOCKCHAINSTATUS_ASYNC));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function0<ResultOrErrorFuture<List<Peer>>> listPeersFunction =
      getStrategyChain().apply(
          identify(blockchainBaseTemplate.getListPeersFunction(), BLOCKCHAIN_LISTPEERS_ASYNC));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function0<ResultOrErrorFuture<List<PeerMetric>>> listPeerMetricsFunction =
      getStrategyChain().apply(
          identify(blockchainBaseTemplate.getListPeersMetricsFunction(),
              BLOCKCHAIN_PEERMETRICS_ASYNC));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function0<ResultOrErrorFuture<NodeStatus>> nodeStatusFunction =
      getStrategyChain().apply(
          identify(blockchainBaseTemplate.getNodeStatusFunction(), BLOCKCHAIN_NODESTATUS_ASYNC));

  @Override
  public ResultOrErrorFuture<BlockchainStatus> getBlockchainStatus() {
    return getBlockchainStatusFunction().apply();
  }

  @Override
  public ResultOrErrorFuture<List<Peer>> listPeers() {
    return getListPeersFunction().apply();
  }

  @Override
  public ResultOrErrorFuture<List<PeerMetric>> listPeerMetrics() {
    return getListPeerMetricsFunction().apply();
  }

  @Override
  public ResultOrErrorFuture<NodeStatus> getNodeStatus() {
    return getNodeStatusFunction().apply();
  }

}
