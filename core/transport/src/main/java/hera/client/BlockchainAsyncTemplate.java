/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static com.google.common.util.concurrent.Futures.addCallback;
import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static hera.util.TransportUtils.longToByteArray;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;
import static types.AergoRPCServiceGrpc.newFutureStub;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.ByteString;
import hera.Context;
import hera.FutureChain;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.BlockchainAsyncOperation;
import hera.api.model.BlockchainStatus;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.transport.BlockchainStatusConverterFactory;
import hera.transport.ModelConverter;
import hera.transport.NodeStatusConverterFactory;
import hera.transport.PeerConverterFactory;
import io.grpc.ManagedChannel;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Node;
import types.Rpc;
import types.Rpc.Empty;
import types.Rpc.PeerList;
import types.Rpc.SingleBytes;

@ApiAudience.Private
@ApiStability.Unstable
@SuppressWarnings("unchecked")
@RequiredArgsConstructor
public class BlockchainAsyncTemplate implements BlockchainAsyncOperation, ChannelInjectable {

  protected final Logger logger = getLogger(getClass());

  protected final ModelConverter<BlockchainStatus, Rpc.BlockchainStatus> blockchainConverter =
      new BlockchainStatusConverterFactory().create();

  protected final ModelConverter<Peer, Node.PeerAddress> peerConverter =
      new PeerConverterFactory().create();

  protected final ModelConverter<NodeStatus, Rpc.SingleBytes> nodeStatusConverter =
      new NodeStatusConverterFactory().create();

  @Setter
  protected Context context;

  @Getter
  protected AergoRPCServiceFutureStub aergoService;

  @Override
  public void injectChannel(final ManagedChannel channel) {
    this.aergoService = newFutureStub(channel);
  }

  @Override
  public ResultOrErrorFuture<BlockchainStatus> getBlockchainStatus() {
    ResultOrErrorFuture<BlockchainStatus> nextFuture =
        ResultOrErrorFutureFactory.supplyEmptyFuture();

    final Empty empty = Empty.newBuilder().build();
    ListenableFuture<Rpc.BlockchainStatus> listenableFuture = aergoService.blockchain(empty);
    FutureChain<Rpc.BlockchainStatus, BlockchainStatus> callback = new FutureChain<>(nextFuture,
        blockchainConverter::convertToDomainModel);
    addCallback(listenableFuture, callback, directExecutor());

    return nextFuture;
  }

  @Override
  public ResultOrErrorFuture<List<Peer>> listPeers() {
    ResultOrErrorFuture<List<Peer>> nextFuture =
        ResultOrErrorFutureFactory.supplyEmptyFuture();

    final Empty empty = Empty.newBuilder().build();
    ListenableFuture<PeerList> listenableFuture = aergoService.getPeers(empty);
    FutureChain<PeerList, List<Peer>> callback =
        new FutureChain<>(nextFuture, peerlist -> peerlist.getPeersList().stream()
            .map(peerConverter::convertToDomainModel).collect(toList()));
    addCallback(listenableFuture, callback, directExecutor());

    return nextFuture;
  }

  @Override
  public ResultOrErrorFuture<NodeStatus> getNodeStatus() {
    ResultOrErrorFuture<NodeStatus> nextFuture = ResultOrErrorFutureFactory.supplyEmptyFuture();

    ByteString byteString = ByteString.copyFrom(longToByteArray(3000L));
    SingleBytes rawTimeout = SingleBytes.newBuilder().setValue(byteString).build();
    ListenableFuture<Rpc.SingleBytes> listenableFuture = aergoService.nodeState(rawTimeout);
    FutureChain<SingleBytes, NodeStatus> callback = new FutureChain<>(nextFuture,
        nodeStatusConverter::convertToDomainModel);
    addCallback(listenableFuture, callback, directExecutor());

    return nextFuture;
  }

}
