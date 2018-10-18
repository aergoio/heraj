/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.Context;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.BlockChainEitherOperation;
import hera.api.model.BlockchainStatus;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.Time;
import hera.api.tupleorerror.ResultOrError;
import hera.strategy.TimeoutStrategy;
import io.grpc.ManagedChannel;
import java.util.List;
import lombok.Getter;

@ApiAudience.Private
@ApiStability.Unstable
public class BlockChainEitherTemplate implements BlockChainEitherOperation, ChannelInjectable {

  protected Context context;

  protected BlockChainAsyncTemplate blockChainAsyncOperation = new BlockChainAsyncTemplate();

  @Getter(lazy = true)
  private final Time timeout =
      context.getStrategy(TimeoutStrategy.class).map(TimeoutStrategy::getTimeout).get();

  @Override
  public void setContext(final Context context) {
    this.context = context;
    blockChainAsyncOperation.setContext(context);
  }

  @Override
  public void injectChannel(final ManagedChannel channel) {
    blockChainAsyncOperation.injectChannel(channel);
  }

  @Override
  public ResultOrError<BlockchainStatus> getBlockchainStatus() {
    return blockChainAsyncOperation.getBlockchainStatus().get(getTimeout().getValue(),
        getTimeout().getUnit());
  }

  @Override
  public ResultOrError<List<Peer>> listPeers() {
    return blockChainAsyncOperation.listPeers().get(getTimeout().getValue(),
        getTimeout().getUnit());
  }

  @Override
  public ResultOrError<NodeStatus> getNodeStatus() {
    return blockChainAsyncOperation.getNodeStatus().get(getTimeout().getValue(),
        getTimeout().getUnit());
  }

}
