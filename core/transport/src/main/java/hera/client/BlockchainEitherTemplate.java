/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.Context;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.BlockchainEitherOperation;
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
public class BlockchainEitherTemplate implements BlockchainEitherOperation, ChannelInjectable {

  protected Context context;

  protected BlockchainAsyncTemplate blockchainAsyncOperation = new BlockchainAsyncTemplate();

  @Getter(lazy = true)
  private final Time timeout =
      context.getStrategy(TimeoutStrategy.class).map(TimeoutStrategy::getTimeout).get();

  @Override
  public void setContext(final Context context) {
    this.context = context;
    blockchainAsyncOperation.setContext(context);
  }

  @Override
  public void injectChannel(final ManagedChannel channel) {
    blockchainAsyncOperation.injectChannel(channel);
  }

  @Override
  public ResultOrError<BlockchainStatus> getBlockchainStatus() {
    return blockchainAsyncOperation.getBlockchainStatus().get(getTimeout().getValue(),
        getTimeout().getUnit());
  }

  @Override
  public ResultOrError<List<Peer>> listPeers() {
    return blockchainAsyncOperation.listPeers().get(getTimeout().getValue(),
        getTimeout().getUnit());
  }

  @Override
  public ResultOrError<NodeStatus> getNodeStatus() {
    return blockchainAsyncOperation.getNodeStatus().get(getTimeout().getValue(),
        getTimeout().getUnit());
  }

}
