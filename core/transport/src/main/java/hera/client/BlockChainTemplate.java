/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static types.AergoRPCServiceGrpc.newFutureStub;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.BlockChainEitherOperation;
import hera.api.BlockChainOperation;
import hera.api.model.BlockchainStatus;
import hera.api.model.NodeStatus;
import hera.api.model.PeerAddress;
import io.grpc.ManagedChannel;
import java.util.List;
import lombok.RequiredArgsConstructor;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;

@ApiAudience.Private
@ApiStability.Unstable
@RequiredArgsConstructor
public class BlockChainTemplate implements BlockChainOperation {

  protected final BlockChainEitherOperation blockChainEitherOperation;

  public BlockChainTemplate(final ManagedChannel channel) {
    this(newFutureStub(channel));
  }

  public BlockChainTemplate(final AergoRPCServiceFutureStub aergoService) {
    this(new BlockChainEitherTemplate(aergoService));
  }

  @Override
  public BlockchainStatus getBlockchainStatus() {
    return blockChainEitherOperation.getBlockchainStatus().getResult();
  }

  @Override
  public List<PeerAddress> listPeers() {
    return blockChainEitherOperation.listPeers().getResult();
  }

  @Override
  public NodeStatus getNodeStatus() {
    return blockChainEitherOperation.getNodeStatus().getResult();
  }

}
