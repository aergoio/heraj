/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.BLOCKCHAIN_BLOCKCHAINSTATUS_EITHER;
import static hera.TransportConstants.BLOCKCHAIN_LISTPEERS_EITHER;
import static hera.TransportConstants.BLOCKCHAIN_NODESTATUS_EITHER;
import static hera.TransportConstants.BLOCKCHAIN_PEERMETRICS_EITHER;
import static hera.api.tupleorerror.Functions.identify;

import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.BlockchainEitherOperation;
import hera.api.model.BlockchainStatus;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.PeerMetric;
import hera.api.tupleorerror.Function0;
import hera.api.tupleorerror.ResultOrError;
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
public class BlockchainEitherTemplate
    implements BlockchainEitherOperation, ChannelInjectable, ContextProviderInjectable {

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
          BLOCKCHAIN_BLOCKCHAINSTATUS_EITHER));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function0<ResultOrErrorFuture<List<Peer>>> listPeersFunction =
      getStrategyChain().apply(
          identify(blockchainBaseTemplate.getListPeersFunction(), BLOCKCHAIN_LISTPEERS_EITHER));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function0<ResultOrErrorFuture<List<PeerMetric>>> listPeerMetricsFunction =
      getStrategyChain().apply(
          identify(blockchainBaseTemplate.getListPeersMetricsFunction(),
              BLOCKCHAIN_PEERMETRICS_EITHER));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function0<ResultOrErrorFuture<NodeStatus>> nodeStatusFunction =
      getStrategyChain().apply(
          identify(blockchainBaseTemplate.getNodeStatusFunction(), BLOCKCHAIN_NODESTATUS_EITHER));

  @Override
  public ResultOrError<BlockchainStatus> getBlockchainStatus() {
    return getBlockchainStatusFunction().apply().get();
  }

  @Override
  public ResultOrError<List<Peer>> listPeers() {
    return getListPeersFunction().apply().get();
  }

  @Override
  public ResultOrError<List<PeerMetric>> listPeerMetrics() {
    return getListPeerMetricsFunction().apply().get();
  }

  @Override
  public ResultOrError<NodeStatus> getNodeStatus() {
    return getNodeStatusFunction().apply().get();
  }

}
