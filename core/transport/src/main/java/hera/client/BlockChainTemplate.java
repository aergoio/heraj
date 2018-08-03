/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.stream.Collectors.toList;
import static types.AergoRPCServiceGrpc.newBlockingStub;

import hera.api.BlockChainOperation;
import hera.api.model.BlockchainStatus;
import hera.api.model.NodeStatus;
import hera.api.model.PeerAddress;
import hera.transport.BlockchainConverterFactory;
import hera.transport.ModelConverter;
import hera.transport.NodeStatusConverterFactory;
import hera.transport.PeerAddressConverterFactory;
import io.grpc.ManagedChannel;
import java.util.List;
import lombok.RequiredArgsConstructor;
import types.AergoRPCServiceGrpc.AergoRPCServiceBlockingStub;
import types.Node;
import types.Rpc;
import types.Rpc.Empty;

@RequiredArgsConstructor
public class BlockChainTemplate implements BlockChainOperation {

  protected final AergoRPCServiceBlockingStub aergoService;

  protected final ModelConverter<BlockchainStatus, Rpc.BlockchainStatus> blockchainConverter;

  protected final ModelConverter<PeerAddress, Node.PeerAddress> peerAddressConverter;

  protected final ModelConverter<NodeStatus, Rpc.NodeStatus> nodeStatusConverter;

  public BlockChainTemplate(final ManagedChannel channel) {
    this(newBlockingStub(channel));
  }

  public BlockChainTemplate(AergoRPCServiceBlockingStub aergoService) {
    this(aergoService, new BlockchainConverterFactory().create(),
        new PeerAddressConverterFactory().create(), new NodeStatusConverterFactory().create());
  }

  @Override
  public BlockchainStatus getBlockchainStatus() {
    final Empty empty = Empty.newBuilder().build();
    return blockchainConverter.convertToDomainModel(aergoService.blockchain(empty));
  }

  @Override
  public List<PeerAddress> listPeers() {
    final Empty empty = Empty.newBuilder().build();
    return aergoService.getPeers(empty).getPeersList().stream()
        .map(peerAddressConverter::convertToDomainModel)
        .collect(toList());
  }

  @Override
  public NodeStatus getNodeStatus() {
    final Empty empty = Empty.newBuilder().build();
    return nodeStatusConverter.convertToDomainModel(aergoService.nodeState(empty));
  }
}
