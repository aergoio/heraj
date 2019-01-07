/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static com.google.common.util.concurrent.Futures.addCallback;
import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static hera.util.TransportUtils.longToByteArray;
import static org.slf4j.LoggerFactory.getLogger;
import static types.AergoRPCServiceGrpc.newFutureStub;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.ByteString;
import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.BlockchainStatus;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.PeerMetric;
import hera.api.tupleorerror.Function0;
import hera.api.tupleorerror.Function1;
import hera.transport.BlockchainStatusConverterFactory;
import hera.transport.ModelConverter;
import hera.transport.NodeStatusConverterFactory;
import hera.transport.PeerConverterFactory;
import hera.transport.PeerMetricConverterFactory;
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

  protected final Logger logger = getLogger(getClass());

  protected final ModelConverter<BlockchainStatus, Rpc.BlockchainStatus> blockchainConverter =
      new BlockchainStatusConverterFactory().create();

  protected final ModelConverter<Peer, Rpc.Peer> peerConverter =
      new PeerConverterFactory().create();

  protected final ModelConverter<PeerMetric, Metric.PeerMetric> peerMetricConverter =
      new PeerMetricConverterFactory().create();

  protected final ModelConverter<NodeStatus, Rpc.SingleBytes> nodeStatusConverter =
      new NodeStatusConverterFactory().create();

  @Getter
  protected AergoRPCServiceFutureStub aergoService;

  protected ContextProvider contextProvider;

  @Override
  public void setChannel(final ManagedChannel channel) {
    this.aergoService = newFutureStub(channel);
  }

  @Override
  public void setContextProvider(final ContextProvider contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Getter
  private final Function0<FinishableFuture<BlockchainStatus>> blockchainStatusFunction =
      new Function0<FinishableFuture<BlockchainStatus>>() {

        @Override
        public FinishableFuture<BlockchainStatus> apply() {
          if (logger.isDebugEnabled()) {
            logger.debug("Get blockchain status, Context: {}", contextProvider.get());
          }

          FinishableFuture<BlockchainStatus> nextFuture = new FinishableFuture<BlockchainStatus>();
          try {
            final Rpc.Empty empty = Rpc.Empty.newBuilder().build();
            ListenableFuture<Rpc.BlockchainStatus> listenableFuture =
                aergoService.blockchain(empty);

            FutureChain<Rpc.BlockchainStatus, BlockchainStatus> callback =
                new FutureChain<>(nextFuture, contextProvider.get());
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
  private final Function0<FinishableFuture<List<Peer>>> listPeersFunction =
      new Function0<FinishableFuture<List<Peer>>>() {

        @Override
        public FinishableFuture<List<Peer>> apply() {
          if (logger.isDebugEnabled()) {
            logger.debug("List peers, Context: {}", contextProvider.get());
          }

          FinishableFuture<List<Peer>> nextFuture = new FinishableFuture<List<Peer>>();
          try {
            final Rpc.Empty empty = Rpc.Empty.newBuilder().build();
            ListenableFuture<Rpc.PeerList> listenableFuture = aergoService.getPeers(empty);

            FutureChain<Rpc.PeerList, List<Peer>> callback =
                new FutureChain<>(nextFuture, contextProvider.get());
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
          if (logger.isDebugEnabled()) {
            logger.debug("List peer metrics, Context: {}", contextProvider.get());
          }
          FinishableFuture<List<PeerMetric>> nextFuture = new FinishableFuture<List<PeerMetric>>();
          try {
            final Metric.MetricsRequest request =
                Metric.MetricsRequest.newBuilder().addTypes(Metric.MetricType.P2P_NETWORK).build();
            ListenableFuture<Metric.Metrics> listenableFuture = aergoService.metric(request);

            FutureChain<Metric.Metrics, List<PeerMetric>> callback =
                new FutureChain<>(nextFuture, contextProvider.get());
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
  private final Function0<FinishableFuture<NodeStatus>> nodeStatusFunction =
      new Function0<FinishableFuture<NodeStatus>>() {

        @Override
        public FinishableFuture<NodeStatus> apply() {
          if (logger.isDebugEnabled()) {
            logger.debug("Get node status, Context: {}", contextProvider.get());
          }

          FinishableFuture<NodeStatus> nextFuture = new FinishableFuture<NodeStatus>();
          try {
            final ByteString rawTimeout = ByteString.copyFrom(longToByteArray(3000L));
            final Rpc.NodeReq nodeRequest = Rpc.NodeReq.newBuilder()
                .setTimeout(rawTimeout)
                .build();
            ListenableFuture<Rpc.SingleBytes> listenableFuture =
                aergoService.nodeState(nodeRequest);

            FutureChain<Rpc.SingleBytes, NodeStatus> callback =
                new FutureChain<>(nextFuture, contextProvider.get());
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
