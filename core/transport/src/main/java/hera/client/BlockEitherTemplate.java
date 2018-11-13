/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.Context;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.BlockEitherOperation;
import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockHeader;
import hera.api.tupleorerror.ResultOrError;
import hera.exception.NoStrategyFoundException;
import hera.exception.RpcException;
import hera.strategy.TimeoutStrategy;
import io.grpc.ManagedChannel;
import java.util.List;

@ApiAudience.Private
@ApiStability.Unstable
public class BlockEitherTemplate implements BlockEitherOperation, ChannelInjectable {

  protected Context context;

  protected BlockAsyncTemplate blockAsyncOperation = new BlockAsyncTemplate();

  @Override
  public void setContext(final Context context) {
    this.context = context;
    blockAsyncOperation.setContext(context);
  }

  @Override
  public void injectChannel(final ManagedChannel channel) {
    blockAsyncOperation.injectChannel(channel);
  }

  @Override
  public ResultOrError<Block> getBlock(BlockHash blockHash) {
    return context.getStrategy(TimeoutStrategy.class)
        .map(f -> f.submit(blockAsyncOperation.getBlock(blockHash)))
        .orElseThrow(() -> new RpcException(new NoStrategyFoundException(TimeoutStrategy.class)));
  }

  @Override
  public ResultOrError<Block> getBlock(final long height) {
    return context.getStrategy(TimeoutStrategy.class)
        .map(f -> f.submit(blockAsyncOperation.getBlock(height)))
        .orElseThrow(() -> new RpcException(new NoStrategyFoundException(TimeoutStrategy.class)));
  }

  @Override
  public ResultOrError<List<BlockHeader>> listBlockHeaders(BlockHash blockHash, int size) {
    return context.getStrategy(TimeoutStrategy.class)
        .map(f -> f.submit(blockAsyncOperation.listBlockHeaders(blockHash, size)))
        .orElseThrow(() -> new RpcException(new NoStrategyFoundException(TimeoutStrategy.class)));
  }

  @Override
  public ResultOrError<List<BlockHeader>> listBlockHeaders(long height, int size) {
    return context.getStrategy(TimeoutStrategy.class)
        .map(f -> f.submit(blockAsyncOperation.listBlockHeaders(height, size)))
        .orElseThrow(() -> new RpcException(new NoStrategyFoundException(TimeoutStrategy.class)));
  }

}

