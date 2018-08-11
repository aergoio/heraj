/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.stream.Collectors.toList;
import static types.AergoRPCServiceGrpc.newFutureStub;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import hera.FutureChainer;
import hera.api.BlockChainAsyncOperation;
import hera.api.model.BlockchainStatus;
import hera.api.model.NodeStatus;
import hera.api.model.PeerAddress;
import hera.transport.BlockchainConverterFactory;
import hera.transport.ModelConverter;
import hera.transport.NodeStatusConverterFactory;
import hera.transport.PeerAddressConverterFactory;
import io.grpc.ManagedChannel;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Node;
import types.Rpc;
import types.Rpc.Empty;
import types.Rpc.PeerList;

@RequiredArgsConstructor
public class BlockChainAsyncTemplate implements BlockChainAsyncOperation {

  protected final AergoRPCServiceFutureStub aergoService;

  protected final ModelConverter<BlockchainStatus, Rpc.BlockchainStatus> blockchainConverter;

  protected final ModelConverter<PeerAddress, Node.PeerAddress> peerAddressConverter;

  protected final ModelConverter<NodeStatus, Rpc.NodeStatus> nodeStatusConverter;

  public BlockChainAsyncTemplate(final ManagedChannel channel) {
    this(newFutureStub(channel));
  }

  public BlockChainAsyncTemplate(AergoRPCServiceFutureStub aergoService) {
    this(aergoService, new BlockchainConverterFactory().create(),
        new PeerAddressConverterFactory().create(), new NodeStatusConverterFactory().create());
  }

  @Override
  public CompletableFuture<BlockchainStatus> getBlockchainStatus() {
    CompletableFuture<BlockchainStatus> nextFuture = new CompletableFuture<>();

    final Empty empty = Empty.newBuilder().build();
    ListenableFuture<Rpc.BlockchainStatus> listenableFuture = aergoService.blockchain(empty);
    FutureChainer<Rpc.BlockchainStatus, BlockchainStatus> callback = new FutureChainer<>(nextFuture,
        blockchainStatus -> blockchainConverter.convertToDomainModel(blockchainStatus));
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;
  }

  @Override
  public CompletableFuture<List<PeerAddress>> listPeers() {
    CompletableFuture<List<PeerAddress>> nextFuture = new CompletableFuture<>();

    final Empty empty = Empty.newBuilder().build();
    ListenableFuture<PeerList> listenableFuture = aergoService.getPeers(empty);
    FutureChainer<PeerList, List<PeerAddress>> callback = new FutureChainer<>(nextFuture,
        peerlist -> peerlist.getPeersList().stream()
            .map(peerAddressConverter::convertToDomainModel)
            .collect(toList()));
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;
  }

  @Override
  public CompletableFuture<NodeStatus> getNodeStatus() {
    CompletableFuture<NodeStatus> nextFuture = new CompletableFuture<>();

    final Empty empty = Empty.newBuilder().build();
    ListenableFuture<Rpc.NodeStatus> listenableFuture = aergoService.nodeState(empty);
    FutureChainer<Rpc.NodeStatus, NodeStatus> callback = new FutureChainer<>(nextFuture,
        nodeStatus -> nodeStatusConverter.convertToDomainModel(nodeStatus));
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;
  }
}
