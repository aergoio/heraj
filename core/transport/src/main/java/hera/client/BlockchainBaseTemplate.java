/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static com.google.common.util.concurrent.Futures.addCallback;
import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static hera.api.tupleorerror.FunctionChain.of;
import static hera.util.TransportUtils.longToByteArray;
import static java.util.stream.Collectors.toList;
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
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.transport.BlockchainStatusConverterFactory;
import hera.transport.ModelConverter;
import hera.transport.NodeStatusConverterFactory;
import hera.transport.PeerConverterFactory;
import hera.transport.PeerMetricConverterFactory;
import io.grpc.ManagedChannel;
import java.util.List;
import lombok.Getter;
import org.slf4j.Logger;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Metric;
import types.Rpc;

@ApiAudience.Private
@ApiStability.Unstable
@SuppressWarnings("unchecked")
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
  private final Function0<ResultOrErrorFuture<BlockchainStatus>> blockchainStatusFunction = () -> {
    ResultOrErrorFuture<BlockchainStatus> nextFuture =
        ResultOrErrorFutureFactory.supplyEmptyFuture();
    logger.debug("Get blockchain status, Context: {}", contextProvider.get());

    final Rpc.Empty empty = Rpc.Empty.newBuilder().build();
    ListenableFuture<Rpc.BlockchainStatus> listenableFuture =
        aergoService.blockchain(empty);
    FutureChain<Rpc.BlockchainStatus, BlockchainStatus> callback =
        new FutureChain<>(nextFuture, contextProvider.get());
    callback.setSuccessHandler(s -> of(() -> blockchainConverter.convertToDomainModel(s)));
    addCallback(listenableFuture, callback, directExecutor());

    return nextFuture;
  };

  @Getter
  private final Function0<ResultOrErrorFuture<List<Peer>>> listPeersFunction = () -> {
    ResultOrErrorFuture<List<Peer>> nextFuture = ResultOrErrorFutureFactory.supplyEmptyFuture();
    logger.debug("List peers, Context: {}", contextProvider.get());

    final Rpc.Empty empty = Rpc.Empty.newBuilder().build();
    ListenableFuture<Rpc.PeerList> listenableFuture = aergoService.getPeers(empty);
    FutureChain<Rpc.PeerList, List<Peer>> callback =
        new FutureChain<>(nextFuture, contextProvider.get());
    callback.setSuccessHandler(peerlist -> of(() -> peerlist.getPeersList().stream()
        .map(peerConverter::convertToDomainModel).collect(toList())));
    addCallback(listenableFuture, callback, directExecutor());

    return nextFuture;
  };

  @Getter
  private final Function0<ResultOrErrorFuture<List<PeerMetric>>> listPeersMetricsFunction = () -> {
    ResultOrErrorFuture<List<PeerMetric>> nextFuture =
        ResultOrErrorFutureFactory.supplyEmptyFuture();
    logger.debug("List peer metrics, Context: {}", contextProvider.get());

    ListenableFuture<Metric.Metrics> listenableFuture = aergoService.metric(
        Metric.MetricsRequest.newBuilder().addTypes(Metric.MetricType.P2P_NETWORK).build());
    FutureChain<Metric.Metrics, List<PeerMetric>> callback =
        new FutureChain<>(nextFuture, contextProvider.get());
    callback.setSuccessHandler(peerlist -> of(() -> peerlist.getPeersList().stream()
        .map(peerMetricConverter::convertToDomainModel).collect(toList())));
    addCallback(listenableFuture, callback, directExecutor());

    return nextFuture;
  };

  @Getter
  private final Function0<ResultOrErrorFuture<NodeStatus>> nodeStatusFunction = () -> {
    ResultOrErrorFuture<NodeStatus> nextFuture = ResultOrErrorFutureFactory.supplyEmptyFuture();
    logger.debug("Get node status, Context: {}", contextProvider.get());

    ByteString rawTimeout = ByteString.copyFrom(longToByteArray(3000L));
    Rpc.NodeReq nodeRequest = Rpc.NodeReq.newBuilder()
        .setTimeout(rawTimeout)
        .build();
    ListenableFuture<Rpc.SingleBytes> listenableFuture = aergoService.nodeState(nodeRequest);
    FutureChain<Rpc.SingleBytes, NodeStatus> callback =
        new FutureChain<>(nextFuture, contextProvider.get());
    callback
        .setSuccessHandler(
            status -> of(() -> nodeStatusConverter.convertToDomainModel(status)));
    addCallback(listenableFuture, callback, directExecutor());

    return nextFuture;
  };

}
