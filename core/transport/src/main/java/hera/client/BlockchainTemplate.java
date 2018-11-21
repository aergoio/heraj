/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.StrategyAcceptable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.BlockchainOperation;
import hera.api.model.BlockchainStatus;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.strategy.StrategyChain;
import io.grpc.ManagedChannel;
import java.util.List;

@ApiAudience.Private
@ApiStability.Unstable
public class BlockchainTemplate
    implements BlockchainOperation, ChannelInjectable, StrategyAcceptable {

  protected BlockchainEitherTemplate blockchainEitherOperation =
      new BlockchainEitherTemplate();

  @Override
  public void accept(final StrategyChain strategyChain) {
    blockchainEitherOperation.accept(strategyChain);
  }

  @Override
  public void injectChannel(final ManagedChannel channel) {
    blockchainEitherOperation.injectChannel(channel);
  }

  @Override
  public BlockchainStatus getBlockchainStatus() {
    return blockchainEitherOperation.getBlockchainStatus().getResult();
  }

  @Override
  public List<Peer> listPeers() {
    return blockchainEitherOperation.listPeers().getResult();
  }

  @Override
  public NodeStatus getNodeStatus() {
    return blockchainEitherOperation.getNodeStatus().getResult();
  }

}
