/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static com.google.common.base.Suppliers.memoize;
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
import hera.api.BlockchainAsyncOperation;
import hera.api.model.BlockchainStatus;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.tupleorerror.Function0;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.strategy.StrategyChain;
import hera.transport.BlockchainStatusConverterFactory;
import hera.transport.ModelConverter;
import hera.transport.NodeStatusConverterFactory;
import hera.transport.PeerConverterFactory;
import io.grpc.ManagedChannel;
import java.util.List;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Rpc;

@ApiAudience.Private
@ApiStability.Unstable
@SuppressWarnings("unchecked")
@RequiredArgsConstructor
public class BlockchainAsyncTemplate
    implements BlockchainAsyncOperation, ChannelInjectable, ContextProviderInjectable {

  protected final Logger logger = getLogger(getClass());

  protected final ModelConverter<BlockchainStatus, Rpc.BlockchainStatus> blockchainConverter =
      new BlockchainStatusConverterFactory().create();

  protected final ModelConverter<Peer, Rpc.Peer> peerConverter =
      new PeerConverterFactory().create();

  protected final ModelConverter<NodeStatus, Rpc.SingleBytes> nodeStatusConverter =
      new NodeStatusConverterFactory().create();

  @Getter
  protected AergoRPCServiceFutureStub aergoService;

  @Setter
  protected ContextProvider contextProvider;

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final StrategyChain strategyChain = StrategyChain.of(contextProvider.get());

  @Override
  public void setChannel(final ManagedChannel channel) {
    this.aergoService = newFutureStub(channel);
  }

  private final Function0<ResultOrErrorFuture<BlockchainStatus>> blockchainStatusFunction = () -> {
    ResultOrErrorFuture<BlockchainStatus> nextFuture =
        ResultOrErrorFutureFactory.supplyEmptyFuture();

    final Rpc.Empty empty = Rpc.Empty.newBuilder().build();
    ListenableFuture<Rpc.BlockchainStatus> listenableFuture =
        aergoService.blockchain(empty);
    FutureChain<Rpc.BlockchainStatus, BlockchainStatus> callback =
        new FutureChain<>(nextFuture);
    callback.setSuccessHandler(s -> of(() -> blockchainConverter.convertToDomainModel(s)));
    addCallback(listenableFuture, callback, directExecutor());

    return nextFuture;
  };

  private final Function0<ResultOrErrorFuture<List<Peer>>> listPeersFunction = () -> {
    ResultOrErrorFuture<List<Peer>> nextFuture = ResultOrErrorFutureFactory.supplyEmptyFuture();

    final Rpc.Empty empty = Rpc.Empty.newBuilder().build();
    ListenableFuture<Rpc.PeerList> listenableFuture = aergoService.getPeers(empty);
    FutureChain<Rpc.PeerList, List<Peer>> callback = new FutureChain<>(nextFuture);
    callback.setSuccessHandler(peerlist -> of(() -> peerlist.getPeersList().stream()
        .map(peerConverter::convertToDomainModel).collect(toList())));
    addCallback(listenableFuture, callback, directExecutor());

    return nextFuture;
  };

  private final Function0<ResultOrErrorFuture<NodeStatus>> nodeStatusFunction = () -> {
    ResultOrErrorFuture<NodeStatus> nextFuture = ResultOrErrorFutureFactory.supplyEmptyFuture();

    ByteString byteString = ByteString.copyFrom(longToByteArray(3000L));
    Rpc.SingleBytes rawTimeout = Rpc.SingleBytes.newBuilder().setValue(byteString).build();
    ListenableFuture<Rpc.SingleBytes> listenableFuture = aergoService.nodeState(rawTimeout);
    FutureChain<Rpc.SingleBytes, NodeStatus> callback = new FutureChain<>(nextFuture);
    callback
        .setSuccessHandler(
            status -> of(() -> nodeStatusConverter.convertToDomainModel(status)));
    addCallback(listenableFuture, callback, directExecutor());

    return nextFuture;
  };

  protected Supplier<
      Function0<ResultOrErrorFuture<BlockchainStatus>>> blockchainStatusFunctionSupplier =
          memoize(() -> getStrategyChain().apply(blockchainStatusFunction));

  protected Supplier<Function0<ResultOrErrorFuture<List<Peer>>>> listPeersFunctionSupplier =
      memoize(() -> getStrategyChain().apply(listPeersFunction));

  protected Supplier<Function0<ResultOrErrorFuture<NodeStatus>>> nodeStatusFunctionSupplier =
      memoize(() -> getStrategyChain().apply(nodeStatusFunction));

  @Override
  public ResultOrErrorFuture<BlockchainStatus> getBlockchainStatus() {
    return blockchainStatusFunctionSupplier.get().apply();
  }

  @Override
  public ResultOrErrorFuture<List<Peer>> listPeers() {
    return listPeersFunctionSupplier.get().apply();
  }

  @Override
  public ResultOrErrorFuture<NodeStatus> getNodeStatus() {
    return nodeStatusFunctionSupplier.get().apply();
  }

}
