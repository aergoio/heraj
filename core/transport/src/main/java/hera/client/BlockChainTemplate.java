/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.stream.Collectors.toList;

import hera.api.BlockChainOperation;
import hera.api.model.BlockchainStatus;
import hera.api.model.PeerAddress;
import hera.transport.BlockchainConverterFactory;
import hera.transport.ModelConverter;
import hera.transport.PeerAddressConverterFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import types.AergoRPCServiceGrpc.AergoRPCServiceBlockingStub;
import types.Rpc;
import types.Rpc.Empty;

@RequiredArgsConstructor
public class BlockChainTemplate implements BlockChainOperation {

  protected final AergoRPCServiceBlockingStub aergoService;

  protected final ModelConverter<BlockchainStatus, Rpc.BlockchainStatus> blockchainConverter;

  protected final ModelConverter<PeerAddress, types.Node.PeerAddress> peerAddressConverter;

  public BlockChainTemplate(AergoRPCServiceBlockingStub aergoService) {
    this(aergoService, new BlockchainConverterFactory().create(),
        new PeerAddressConverterFactory().create());
  }

  @Override
  public BlockchainStatus getStatus() {
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
}
