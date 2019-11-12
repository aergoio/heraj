/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.function.Functions.identify;
import static hera.client.ClientConstants.BLOCKCHAIN_BLOCKCHAINSTATUS;
import static hera.client.ClientConstants.BLOCKCHAIN_CHAININFO;
import static hera.client.ClientConstants.BLOCKCHAIN_CHAINSTATS;
import static hera.client.ClientConstants.BLOCKCHAIN_LIST_PEERS;
import static hera.client.ClientConstants.BLOCKCHAIN_NODESTATUS;
import static hera.client.ClientConstants.BLOCKCHAIN_PEERMETRICS;
import static hera.client.ClientConstants.BLOCKCHAIN_SERVERINFO;

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
import hera.exception.RpcException;
import hera.exception.RpcExceptionConverter;
import hera.strategy.PriorityProvider;
import hera.strategy.StrategyApplier;
import hera.util.ExceptionConverter;
import io.grpc.ManagedChannel;
import java.util.List;
import java.util.concurrent.Future;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@ApiAudience.Private
@ApiStability.Unstable
@RequiredArgsConstructor
public class BlockchainTemplate
    implements BlockchainOperation, ChannelInjectable, ContextProviderInjectable {

  protected final ExceptionConverter<RpcException> exceptionConverter = new RpcExceptionConverter();

  protected BlockchainBaseTemplate blockchainBaseTemplate = new BlockchainBaseTemplate();

  protected ContextProvider contextProvider;

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final StrategyApplier strategyApplier =
      StrategyApplier.of(contextProvider.get(), PriorityProvider.get());

  @Override
  public void setChannel(final ManagedChannel channel) {
    this.blockchainBaseTemplate.setChannel(channel);
  }

  @Override
  public void setContextProvider(final ContextProvider contextProvider) {
    this.contextProvider = contextProvider;
    this.blockchainBaseTemplate.setContextProvider(contextProvider);
  }

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function0<Future<BlockchainStatus>> blockchainStatusFunction =
      getStrategyApplier().apply(identify(this.blockchainBaseTemplate.getBlockchainStatusFunction(),
          BLOCKCHAIN_BLOCKCHAINSTATUS));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function0<Future<ChainInfo>> chainInfoFunction =
      getStrategyApplier().apply(identify(this.blockchainBaseTemplate.getChainInfoFunction(),
          BLOCKCHAIN_CHAININFO));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function0<Future<ChainStats>> chainStatsFunction =
      getStrategyApplier().apply(identify(this.blockchainBaseTemplate.getChainStatsFunction(),
          BLOCKCHAIN_CHAINSTATS));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function2<Boolean, Boolean, Future<List<Peer>>> listPeersFunction =
      getStrategyApplier().apply(
          identify(this.blockchainBaseTemplate.getListPeersFunction(), BLOCKCHAIN_LIST_PEERS));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function0<Future<List<PeerMetric>>> listPeerMetricsFunction =
      getStrategyApplier().apply(
          identify(this.blockchainBaseTemplate.getListPeersMetricsFunction(),
              BLOCKCHAIN_PEERMETRICS));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<List<String>, Future<ServerInfo>> serverInfoFunction =
      getStrategyApplier().apply(
          identify(this.blockchainBaseTemplate.getServerInfoFunction(), BLOCKCHAIN_SERVERINFO));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function0<Future<NodeStatus>> nodeStatusFunction =
      getStrategyApplier().apply(
          identify(this.blockchainBaseTemplate.getNodeStatusFunction(), BLOCKCHAIN_NODESTATUS));

  @Override
  public ChainIdHash getChainIdHash() {
    return getBlockchainStatus().getChainIdHash();
  }

  @Override
  public BlockchainStatus getBlockchainStatus() {
    try {
      return getBlockchainStatusFunction().apply().get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public ChainInfo getChainInfo() {
    try {
      return getChainInfoFunction().apply().get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public ChainStats getChainStats() {
    try {
      return getChainStatsFunction().apply().get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public List<Peer> listPeers() {
    try {
      return listPeers(false, false);
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public List<Peer> listPeers(final boolean showHidden, final boolean showSelf) {
    try {
      return getListPeersFunction().apply(showHidden, showSelf).get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public List<PeerMetric> listPeerMetrics() {
    try {
      return getListPeerMetricsFunction().apply().get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public ServerInfo getServerInfo(final List<String> categories) {
    try {
      return getServerInfoFunction().apply(categories).get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public NodeStatus getNodeStatus() {
    try {
      return getNodeStatusFunction().apply().get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

}
