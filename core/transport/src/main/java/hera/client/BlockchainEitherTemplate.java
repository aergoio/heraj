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
import hera.api.tupleorerror.ResultOrError;
import hera.exception.NoStrategyFoundException;
import hera.exception.RpcException;
import hera.strategy.TimeoutStrategy;
import io.grpc.ManagedChannel;
import java.util.List;

@ApiAudience.Private
@ApiStability.Unstable
public class BlockchainEitherTemplate implements BlockchainEitherOperation, ChannelInjectable {

  protected Context context;

  protected BlockchainAsyncTemplate blockchainAsyncOperation = new BlockchainAsyncTemplate();

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
    return context.getStrategy(TimeoutStrategy.class)
        .map(f -> f.submit(blockchainAsyncOperation.getBlockchainStatus()))
        .orElseThrow(() -> new RpcException(new NoStrategyFoundException(TimeoutStrategy.class)));
  }

  @Override
  public ResultOrError<List<Peer>> listPeers() {
    return context.getStrategy(TimeoutStrategy.class)
        .map(f -> f.submit(blockchainAsyncOperation.listPeers()))
        .orElseThrow(() -> new RpcException(new NoStrategyFoundException(TimeoutStrategy.class)));
  }

  @Override
  public ResultOrError<NodeStatus> getNodeStatus() {
    return context.getStrategy(TimeoutStrategy.class)
        .map(f -> f.submit(blockchainAsyncOperation.getNodeStatus()))
        .orElseThrow(() -> new RpcException(new NoStrategyFoundException(TimeoutStrategy.class)));
  }

}
