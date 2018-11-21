/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.StrategyAcceptable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.BlockchainEitherOperation;
import hera.api.model.BlockchainStatus;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.tupleorerror.ResultOrError;
import hera.strategy.StrategyChain;
import io.grpc.ManagedChannel;
import java.util.List;

@ApiAudience.Private
@ApiStability.Unstable
public class BlockchainEitherTemplate
    implements BlockchainEitherOperation, ChannelInjectable, StrategyAcceptable {

  protected BlockchainAsyncTemplate blockchainAsyncOperation = new BlockchainAsyncTemplate();

  @Override
  public void injectChannel(final ManagedChannel channel) {
    blockchainAsyncOperation.injectChannel(channel);
  }

  @Override
  public void accept(final StrategyChain strategyChain) {
    blockchainAsyncOperation.accept(strategyChain);
  }

  @Override
  public ResultOrError<BlockchainStatus> getBlockchainStatus() {
    return blockchainAsyncOperation.getBlockchainStatus().get();
  }

  @Override
  public ResultOrError<List<Peer>> listPeers() {
    return blockchainAsyncOperation.listPeers().get();
  }

  @Override
  public ResultOrError<NodeStatus> getNodeStatus() {
    return blockchainAsyncOperation.getNodeStatus().get();
  }

}
