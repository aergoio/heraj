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
import hera.api.model.Time;
import hera.api.tupleorerror.ResultOrError;
import hera.exception.RpcException;
import hera.strategy.TimeoutStrategy;
import io.grpc.ManagedChannel;
import java.util.List;
import lombok.Getter;

@ApiAudience.Private
@ApiStability.Unstable
public class BlockEitherTemplate implements BlockEitherOperation, ChannelInjectable {

  protected Context context;

  protected BlockAsyncTemplate blockAsyncOperation = new BlockAsyncTemplate();

  @Getter(lazy = true)
  private final Time timeout =
      context.getStrategy(TimeoutStrategy.class).map(TimeoutStrategy::getTimeout)
          .orElseThrow(() -> new RpcException("TimeoutStrategy must be present in context"));

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
    return blockAsyncOperation.getBlock(blockHash).get(getTimeout().getValue(),
        getTimeout().getUnit());
  }

  @Override
  public ResultOrError<Block> getBlock(final long height) {
    return blockAsyncOperation.getBlock(height).get(getTimeout().getValue(),
        getTimeout().getUnit());
  }

  @Override
  public ResultOrError<List<BlockHeader>> listBlockHeaders(BlockHash blockHash, int size) {
    return blockAsyncOperation.listBlockHeaders(blockHash, size).get(getTimeout().getValue(),
        getTimeout().getUnit());
  }

  @Override
  public ResultOrError<List<BlockHeader>> listBlockHeaders(long height, int size) {
    return blockAsyncOperation.listBlockHeaders(height, size).get(getTimeout().getValue(),
        getTimeout().getUnit());
  }

}

