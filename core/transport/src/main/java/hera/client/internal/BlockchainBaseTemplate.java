/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.internal;

import static com.google.common.util.concurrent.Futures.addCallback;
import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static hera.util.TransportUtils.copyFrom;
import static org.slf4j.LoggerFactory.getLogger;
import static types.AergoRPCServiceGrpc.newFutureStub;

import com.google.common.util.concurrent.ListenableFuture;
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
  private final Function0<FinishableFuture<BlockchainStatus>> blockchainStatusFunction =
      new Function0<FinishableFuture<BlockchainStatus>>() {

        @Override
        public FinishableFuture<BlockchainStatus> apply() {
          logger.debug("Get blockchain status");

          FinishableFuture<BlockchainStatus> nextFuture = new FinishableFuture<BlockchainStatus>();
          try {
            final Rpc.Empty empty = Rpc.Empty.newBuilder().build();
            logger.trace("AergoService blockchain arg: {}", empty);

            ListenableFuture<Rpc.BlockchainStatus> listenableFuture =
                aergoService.blockchain(empty);
            FutureChain<Rpc.BlockchainStatus, BlockchainStatus> callback =
                new FutureChain<>(nextFuture);
            callback.setSuccessHandler(new Function1<Rpc.BlockchainStatus, BlockchainStatus>() {

              @Override
              public BlockchainStatus apply(Rpc.BlockchainStatus s) {
                return blockchainConverter.convertToDomainModel(s);
              }
            });
            addCallback(listenableFuture, callback, directExecutor());
          } catch (Exception e) {
            nextFuture.fail(e);
          }
          return nextFuture;
        }
      };

  @Getter
  private final Function0<FinishableFuture<ChainInfo>> chainInfoFunction =
      new Function0<FinishableFuture<ChainInfo>>() {

        @Override
        public FinishableFuture<ChainInfo> apply() {
          logger.debug("Get chain info");

          FinishableFuture<ChainInfo> nextFuture = new FinishableFuture<ChainInfo>();
          try {
            final Rpc.Empty empty = Rpc.Empty.newBuilder().build();
            logger.trace("AergoService getChainInfo arg: {}", empty);

            ListenableFuture<Rpc.ChainInfo> listenableFuture =
                aergoService.getChainInfo(empty);
            FutureChain<Rpc.ChainInfo, ChainInfo> callback = new FutureChain<>(nextFuture);
            callback.setSuccessHandler(new Function1<Rpc.ChainInfo, ChainInfo>() {

              @Override
              public ChainInfo apply(final Rpc.ChainInfo rpcChainInfo) {
                return chainInfoConverter.convertToDomainModel(rpcChainInfo);
              }
            });
            addCallback(listenableFuture, callback, directExecutor());
          } catch (Exception e) {
            nextFuture.fail(e);
          }
          return nextFuture;
        }
      };

  @Getter
  private final Function0<FinishableFuture<ChainStats>> chainStatsFunction =
      new Function0<FinishableFuture<ChainStats>>() {

        @Override
        public FinishableFuture<ChainStats> apply() {
          logger.debug("Get chain info");

          FinishableFuture<ChainStats> nextFuture = new FinishableFuture<ChainStats>();
          try {
            final Rpc.Empty empty = Rpc.Empty.newBuilder().build();
            logger.trace("AergoService getChainStats arg: {}", empty);

            ListenableFuture<Rpc.ChainStats> listenableFuture = aergoService.chainStat(empty);
            FutureChain<Rpc.ChainStats, ChainStats> callback = new FutureChain<>(nextFuture);
            callback.setSuccessHandler(new Function1<Rpc.ChainStats, ChainStats>() {

              @Override
              public ChainStats apply(final Rpc.ChainStats rpcChainStats) {
                return chainStatsConverter.convertToDomainModel(rpcChainStats);
              }
            });
            addCallback(listenableFuture, callback, directExecutor());
          } catch (Exception e) {
            nextFuture.fail(e);
          }
          return nextFuture;
        }
      };

  @Getter
  private final Function2<Boolean, Boolean, FinishableFuture<List<Peer>>> listPeersFunction =
      new Function2<Boolean, Boolean, FinishableFuture<List<Peer>>>() {

        @Override
        public FinishableFuture<List<Peer>> apply(final Boolean showHidden,
            final Boolean showSelf) {
          logger.debug("List peers with showHidden: {}, showSelf: {}", showHidden, showSelf);

          FinishableFuture<List<Peer>> nextFuture = new FinishableFuture<List<Peer>>();
          try {
            final Rpc.PeersParams peersParams = Rpc.PeersParams.newBuilder()
                .setNoHidden(!showHidden.booleanValue())
                .setShowSelf(showSelf.booleanValue())
                .build();
            logger.trace("AergoService getPeers arg: {}", peersParams);

            ListenableFuture<Rpc.PeerList> listenableFuture = aergoService.getPeers(peersParams);
            FutureChain<Rpc.PeerList, List<Peer>> callback = new FutureChain<>(nextFuture);
            callback.setSuccessHandler(new Function1<Rpc.PeerList, List<Peer>>() {

              @Override
              public List<Peer> apply(final Rpc.PeerList peerlist) {
                final List<Peer> domainPeerList = new ArrayList<Peer>();
                for (final Rpc.Peer rpcPeer : peerlist.getPeersList()) {
                  domainPeerList.add(peerConverter.convertToDomainModel(rpcPeer));
                }
                return domainPeerList;
              }
            });
            addCallback(listenableFuture, callback, directExecutor());
          } catch (Exception e) {
            nextFuture.fail(e);
          }
          return nextFuture;
        }
      };

  @Getter
  private final Function0<FinishableFuture<List<PeerMetric>>> listPeersMetricsFunction =
      new Function0<FinishableFuture<List<PeerMetric>>>() {

        @Override
        public FinishableFuture<List<PeerMetric>> apply() {
          logger.debug("List peer metrics");

          FinishableFuture<List<PeerMetric>> nextFuture = new FinishableFuture<List<PeerMetric>>();
          try {
            final Metric.MetricsRequest rpcMetricRequest = Metric.MetricsRequest.newBuilder()
                .addTypes(Metric.MetricType.P2P_NETWORK)
                .build();
            logger.trace("AergoService metric arg: {}", rpcMetricRequest);

            ListenableFuture<Metric.Metrics> listenableFuture =
                aergoService.metric(rpcMetricRequest);
            FutureChain<Metric.Metrics, List<PeerMetric>> callback = new FutureChain<>(nextFuture);
            callback.setSuccessHandler(new Function1<Metric.Metrics, List<PeerMetric>>() {

              @Override
              public List<PeerMetric> apply(final Metric.Metrics rpcMetrics) {
                final List<PeerMetric> domainMetrics = new ArrayList<PeerMetric>();
                for (final Metric.PeerMetric rpcMetric : rpcMetrics.getPeersList()) {
                  domainMetrics.add(peerMetricConverter.convertToDomainModel(rpcMetric));
                }
                return domainMetrics;
              }
            });
            addCallback(listenableFuture, callback, directExecutor());
          } catch (Exception e) {
            nextFuture.fail(e);
          }
          return nextFuture;
        }
      };

  @Getter
  private final Function1<List<String>, FinishableFuture<ServerInfo>> serverInfoFunction =
      new Function1<List<String>, FinishableFuture<ServerInfo>>() {

        @Override
        public FinishableFuture<ServerInfo> apply(final List<String> keys) {
          logger.debug("Get node status");

          FinishableFuture<ServerInfo> nextFuture = new FinishableFuture<ServerInfo>();
          try {
            final Rpc.KeyParams rpcServerInfoRequest = Rpc.KeyParams.newBuilder()
                .addAllKey(keys)
                .build();
            logger.trace("AergoService getServerInfo arg: {}", rpcServerInfoRequest);

            ListenableFuture<Rpc.ServerInfo> listenableFuture =
                aergoService.getServerInfo(rpcServerInfoRequest);
            FutureChain<Rpc.ServerInfo, ServerInfo> callback = new FutureChain<>(nextFuture);
            callback.setSuccessHandler(new Function1<Rpc.ServerInfo, ServerInfo>() {

              @Override
              public ServerInfo apply(final Rpc.ServerInfo status) {
                return serverInfoConverter.convertToDomainModel(status);
              }
            });
            addCallback(listenableFuture, callback, directExecutor());
          } catch (Exception e) {
            nextFuture.fail(e);
          }
          return nextFuture;
        }
      };

  @Getter
  private final Function0<FinishableFuture<NodeStatus>> nodeStatusFunction =
      new Function0<FinishableFuture<NodeStatus>>() {

        @Override
        public FinishableFuture<NodeStatus> apply() {
          logger.debug("Get node status");

          FinishableFuture<NodeStatus> nextFuture = new FinishableFuture<NodeStatus>();
          try {
            final Rpc.NodeReq rpcNodeRequest = Rpc.NodeReq.newBuilder()
                .setTimeout(copyFrom(NODE_STATUS_TIMEOUT))
                .build();
            logger.trace("AergoService nodeState arg: {}", rpcNodeRequest);

            ListenableFuture<Rpc.SingleBytes> listenableFuture =
                aergoService.nodeState(rpcNodeRequest);
            FutureChain<Rpc.SingleBytes, NodeStatus> callback = new FutureChain<>(nextFuture);
            callback.setSuccessHandler(new Function1<Rpc.SingleBytes, NodeStatus>() {

              @Override
              public NodeStatus apply(final Rpc.SingleBytes status) {
                return nodeStatusConverter.convertToDomainModel(status);
              }
            });
            addCallback(listenableFuture, callback, directExecutor());
          } catch (Exception e) {
            nextFuture.fail(e);
          }
          return nextFuture;
        }
      };

}