/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.internal;

import static hera.util.TransportUtils.copyFrom;
import static org.slf4j.LoggerFactory.getLogger;
import static types.AergoRPCServiceGrpc.newFutureStub;

import com.google.protobuf.ByteString;
import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.function.Function0;
import hera.api.function.Function1;
import hera.api.function.Function2;
import hera.api.model.AccountAddress;
import hera.api.model.AccountTotalVote;
import hera.api.model.BlockchainStatus;
import hera.api.model.ChainInfo;
import hera.api.model.ChainStats;
import hera.api.model.ElectedCandidate;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.PeerMetric;
import hera.api.model.ServerInfo;
import hera.client.ChannelInjectable;
import hera.transport.AccountAddressConverterFactory;
import hera.transport.AccountTotalVoteConverterFactory;
import hera.transport.BlockchainStatusConverterFactory;
import hera.transport.ChainInfoConverterFactory;
import hera.transport.ChainStatsConverterFactory;
import hera.transport.ElectedCandidateConverterFactory;
import hera.transport.ModelConverter;
import hera.transport.NodeStatusConverterFactory;
import hera.transport.PeerConverterFactory;
import hera.transport.PeerMetricConverterFactory;
import hera.transport.ServerInfoConverterFactory;
import io.grpc.ManagedChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import lombok.Getter;
import org.slf4j.Logger;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Metric;
import types.Rpc;

@ApiAudience.Private
@ApiStability.Unstable
public class BlockchainBaseTemplate implements ChannelInjectable, ContextProviderInjectable {

  protected static final long NODE_STATUS_TIMEOUT = 3000L;

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<BlockchainStatus, Rpc.BlockchainStatus> blockchainConverter =
      new BlockchainStatusConverterFactory().create();

  protected final ModelConverter<ChainInfo, Rpc.ChainInfo> chainInfoConverter =
      new ChainInfoConverterFactory().create();

  protected final ModelConverter<ChainStats, Rpc.ChainStats> chainStatsConverter =
      new ChainStatsConverterFactory().create();

  protected final ModelConverter<Peer, Rpc.Peer> peerConverter =
      new PeerConverterFactory().create();

  protected final ModelConverter<PeerMetric, Metric.PeerMetric> peerMetricConverter =
      new PeerMetricConverterFactory().create();

  protected final ModelConverter<ServerInfo, Rpc.ServerInfo> serverInfoConverter =
      new ServerInfoConverterFactory().create();

  protected final ModelConverter<NodeStatus, Rpc.SingleBytes> nodeStatusConverter =
      new NodeStatusConverterFactory().create();

  protected final ModelConverter<AccountAddress, ByteString> accountAddressConverter =
      new AccountAddressConverterFactory().create();

  protected final ModelConverter<ElectedCandidate, Rpc.Vote> electedCandidateConverter =
      new ElectedCandidateConverterFactory().create();

  protected final ModelConverter<AccountTotalVote, Rpc.AccountVoteInfo> accountTotalVoteConverter =
      new AccountTotalVoteConverterFactory().create();

  protected AccountBaseTemplate accountBaseTemplate = new AccountBaseTemplate();

  protected TransactionBaseTemplate transactionBaseTemplate = new TransactionBaseTemplate();

  @Getter
  protected AergoRPCServiceFutureStub aergoService;

  protected ContextProvider contextProvider;

  @Override
  public void setChannel(final ManagedChannel channel) {
    this.aergoService = newFutureStub(channel);
    this.accountBaseTemplate.setChannel(channel);
    this.transactionBaseTemplate.setChannel(channel);
  }

  @Override
  public void setContextProvider(final ContextProvider contextProvider) {
    this.contextProvider = contextProvider;
    this.accountBaseTemplate.setContextProvider(contextProvider);
    this.transactionBaseTemplate.setContextProvider(contextProvider);
  }

  @Getter
  private final Function0<Future<BlockchainStatus>> blockchainStatusFunction =
      new Function0<Future<BlockchainStatus>>() {

        @Override
        public Future<BlockchainStatus> apply() {
          logger.debug("Get blockchain status");

          final Rpc.Empty empty = Rpc.Empty.newBuilder().build();
          logger.trace("AergoService blockchain arg: {}", empty);

          final Future<Rpc.BlockchainStatus> rawFuture = aergoService.blockchain(empty);
          final Future<BlockchainStatus> convertedFuture = HerajFutures.transform(rawFuture,
              new Function1<Rpc.BlockchainStatus, BlockchainStatus>() {
                @Override
                public BlockchainStatus apply(final Rpc.BlockchainStatus rpcBlockchainStatus) {
                  return blockchainConverter.convertToDomainModel(rpcBlockchainStatus);
                }
              });
          return convertedFuture;
        }
      };

  @Getter
  private final Function0<Future<ChainInfo>> chainInfoFunction =
      new Function0<Future<ChainInfo>>() {

        @Override
        public Future<ChainInfo> apply() {
          logger.debug("Get chain info");

          final Rpc.Empty empty = Rpc.Empty.newBuilder().build();
          logger.trace("AergoService getChainInfo arg: {}", empty);

          final Future<Rpc.ChainInfo> rawFuture = aergoService.getChainInfo(empty);
          final Future<ChainInfo> convertedFuture =
              HerajFutures.transform(rawFuture, new Function1<Rpc.ChainInfo, ChainInfo>() {

                @Override
                public ChainInfo apply(final Rpc.ChainInfo rpcChainInfo) {
                  return chainInfoConverter.convertToDomainModel(rpcChainInfo);
                }
              });
          return convertedFuture;
        }
      };

  @Getter
  private final Function0<Future<ChainStats>> chainStatsFunction =
      new Function0<Future<ChainStats>>() {

        @Override
        public Future<ChainStats> apply() {
          logger.debug("Get chain info");

          final Rpc.Empty empty = Rpc.Empty.newBuilder().build();
          logger.trace("AergoService getChainStats arg: {}", empty);

          final Future<Rpc.ChainStats> rawFuture = aergoService.chainStat(empty);
          final Future<ChainStats> convertedFuture =
              HerajFutures.transform(rawFuture, new Function1<Rpc.ChainStats, ChainStats>() {

                @Override
                public ChainStats apply(final Rpc.ChainStats rpcChainStatus) {
                  return chainStatsConverter.convertToDomainModel(rpcChainStatus);
                }
              });
          return convertedFuture;

        }
      };

  @Getter
  private final Function2<Boolean, Boolean, Future<List<Peer>>> listPeersFunction =
      new Function2<Boolean, Boolean, Future<List<Peer>>>() {

        @Override
        public Future<List<Peer>> apply(final Boolean showHidden,
            final Boolean showSelf) {
          logger.debug("List peers with showHidden: {}, showSelf: {}", showHidden, showSelf);

          final Rpc.PeersParams peersParams = Rpc.PeersParams.newBuilder()
              .setNoHidden(!showHidden.booleanValue())
              .setShowSelf(showSelf.booleanValue())
              .build();
          logger.trace("AergoService getPeers arg: {}", peersParams);

          final Future<Rpc.PeerList> rawFuture = aergoService.getPeers(peersParams);
          final Future<List<Peer>> convertedFuture =
              HerajFutures.transform(rawFuture, new Function1<Rpc.PeerList, List<Peer>>() {

                @Override
                public List<Peer> apply(final Rpc.PeerList rpcPeerList) {
                  final List<Peer> domainPeers = new ArrayList<>();
                  for (final Rpc.Peer rpcPeer : rpcPeerList.getPeersList()) {
                    domainPeers.add(peerConverter.convertToDomainModel(rpcPeer));
                  }
                  return domainPeers;
                }
              });
          return convertedFuture;
        }
      };

  @Getter
  private final Function0<Future<List<PeerMetric>>> listPeersMetricsFunction =
      new Function0<Future<List<PeerMetric>>>() {

        @Override
        public Future<List<PeerMetric>> apply() {
          logger.debug("List peer metrics");

          final Metric.MetricsRequest rpcMetricRequest = Metric.MetricsRequest.newBuilder()
              .addTypes(Metric.MetricType.P2P_NETWORK)
              .build();
          logger.trace("AergoService metric arg: {}", rpcMetricRequest);

          final Future<Metric.Metrics> rawFuture = aergoService.metric(rpcMetricRequest);
          final Future<List<PeerMetric>> convertedFuture =
              HerajFutures.transform(rawFuture, new Function1<Metric.Metrics, List<PeerMetric>>() {

                @Override
                public List<PeerMetric> apply(final Metric.Metrics rpcPeerMetrics) {
                  final List<PeerMetric> domainPeerMetrics = new ArrayList<>();
                  for (final Metric.PeerMetric rpcPeerMetric : rpcPeerMetrics.getPeersList()) {
                    domainPeerMetrics.add(peerMetricConverter.convertToDomainModel(rpcPeerMetric));
                  }
                  return domainPeerMetrics;
                }
              });
          return convertedFuture;
        }
      };

  @Getter
  private final Function1<List<String>, Future<ServerInfo>> serverInfoFunction =
      new Function1<List<String>, Future<ServerInfo>>() {

        @Override
        public Future<ServerInfo> apply(final List<String> keys) {
          logger.debug("Get node status");

          final Rpc.KeyParams rpcServerInfoRequest = Rpc.KeyParams.newBuilder()
              .addAllKey(keys)
              .build();
          logger.trace("AergoService getServerInfo arg: {}", rpcServerInfoRequest);

          final Future<Rpc.ServerInfo> rawFuture =
              aergoService.getServerInfo(rpcServerInfoRequest);
          final Future<ServerInfo> convertedFuture =
              HerajFutures.transform(rawFuture, new Function1<Rpc.ServerInfo, ServerInfo>() {

                @Override
                public ServerInfo apply(final Rpc.ServerInfo rpcServerInfo) {
                  return serverInfoConverter.convertToDomainModel(rpcServerInfo);
                }
              });
          return convertedFuture;
        }
      };

  @Getter
  private final Function0<Future<NodeStatus>> nodeStatusFunction =
      new Function0<Future<NodeStatus>>() {

        @Override
        public Future<NodeStatus> apply() {
          logger.debug("Get node status");

          final Rpc.NodeReq rpcNodeRequest = Rpc.NodeReq.newBuilder()
              .setTimeout(copyFrom(NODE_STATUS_TIMEOUT))
              .build();
          logger.trace("AergoService nodeState arg: {}", rpcNodeRequest);

          final Future<Rpc.SingleBytes> rawFuture = aergoService.nodeState(rpcNodeRequest);
          final Future<NodeStatus> convertedFuture =
              HerajFutures.transform(rawFuture, new Function1<Rpc.SingleBytes, NodeStatus>() {

                @Override
                public NodeStatus apply(final Rpc.SingleBytes rpcNodeStatus) {
                  return nodeStatusConverter.convertToDomainModel(rpcNodeStatus);
                }
              });
          return convertedFuture;
        }
      };

}
